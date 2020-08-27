package vstocks.db.portfolio;

import vstocks.db.BaseDB;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.db.UserDB;
import vstocks.model.*;
import vstocks.model.portfolio.MarketRank;
import vstocks.model.portfolio.MarketRankCollection;
import vstocks.model.portfolio.RankedUser;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Delta.getReverseDeltas;
import static vstocks.model.SortDirection.DESC;

class MarketRankDB extends BaseDB {
    private static final String BATCH_SEQUENCE = "market_ranks_batch_sequence";

    private static final RowMapper<MarketRank> ROW_MAPPER = rs ->
            new MarketRank()
                    .setBatch(rs.getLong("batch"))
                    .setUserId(rs.getString("user_id"))
                    .setMarket(Market.valueOf(rs.getString("market")))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setRank(rs.getLong("rank"))
                    .setValue(rs.getLong("value"));

    private static final RowMapper<RankedUser> USER_ROW_MAPPER = rs ->
            new RankedUser()
                    .setUser(UserDB.ROW_MAPPER.map(rs))
                    .setBatch(rs.getLong("batch"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant())
                    .setRank(rs.getLong("rank"))
                    .setValue(rs.getLong("value"));

    private static final RowSetter<MarketRank> INSERT_ROW_SETTER = (ps, marketRank) -> {
        int index = 0;
        ps.setLong(++index, marketRank.getBatch());
        ps.setString(++index, marketRank.getUserId());
        ps.setString(++index, marketRank.getMarket().name());
        ps.setTimestamp(++index, Timestamp.from(marketRank.getTimestamp()));
        ps.setLong(++index, marketRank.getRank());
        ps.setLong(++index, marketRank.getValue());
    };

    @Override
    protected List<Sort> getDefaultSort() {
        return asList(BATCH.toSort(DESC), RANK.toSort(), USER_ID.toSort());
    }

    public long setCurrentBatch(Connection connection, long batch) {
        return setSequenceValue(connection, BATCH_SEQUENCE, batch);
    }

    private int generate(Connection connection, long batch, Market market) {
        String sql = "INSERT INTO market_ranks (batch, user_id, market, timestamp, rank, value)"
                + "(SELECT ? AS batch, user_id, market, timestamp, "
                + "        RANK() OVER (ORDER BY value DESC) AS rank, value FROM ("
                + "  SELECT user_id, market, NOW() AS timestamp, SUM(value) AS value FROM ("
                + "    (SELECT id AS user_id, ? AS market, NULL AS symbol, 0 AS value FROM users)"
                + "    UNION"
                + "    (SELECT DISTINCT ON (us.user_id, us.market, us.symbol)"
                + "      us.user_id, us.market, us.symbol, us.shares * sp.price AS value"
                + "    FROM user_stocks us"
                + "    JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol)"
                + "    WHERE us.market = ?"
                + "    ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC)"
                + "  ) AS priced_stocks GROUP BY user_id, market ORDER BY value DESC"
                + ") AS ordered_values)";
        return update(connection, sql, batch, market, market);
    }

    public int generate(Connection connection) {
        long batch = getNextSequenceValue(connection, BATCH_SEQUENCE);
        return Arrays.stream(Market.values()).mapToInt(market -> generate(connection, batch, market)).sum();
    }

    public MarketRankCollection getLatest(Connection connection, String userId, Market market) {
        Instant earliest = DeltaInterval.getLast().getEarliest();

        String sql = "SELECT * FROM market_ranks WHERE timestamp >= ? AND user_id = ? AND market = ? "
                + "ORDER BY timestamp DESC";
        List<MarketRank> ranks = new ArrayList<>();
        consume(connection, ROW_MAPPER, ranks::add, sql, earliest, userId, market);

        return new MarketRankCollection()
                .setMarket(market)
                .setRanks(ranks)
                .setDeltas(getReverseDeltas(ranks, MarketRank::getTimestamp, MarketRank::getRank));
    }

    public List<MarketRankCollection> getLatest(Connection connection, String userId) {
        List<MarketRankCollection> results = new ArrayList<>(Market.values().length);
        for (Market market : Market.values()) {
            results.add(getLatest(connection, userId, market));
        }
        return results;
    }

    public Results<MarketRank> getAll(Connection connection, Market market, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM market_ranks WHERE market = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM market_ranks WHERE market = ?";
        return results(connection, ROW_MAPPER, page, sql, count, market);
    }

    public Results<RankedUser> getUsers(Connection connection, Market market, Page page) {
        long batch = getCurrentSequenceValue(connection, BATCH_SEQUENCE);
        String sql = "SELECT * FROM market_ranks r JOIN users u ON (u.id = r.user_id) WHERE batch = ? AND market = ? "
                + "ORDER BY r.rank, u.username LIMIT ? OFFSET ?";
        String count = "SELECT COUNT(*) FROM market_ranks WHERE batch = ? AND market = ?";
        return results(connection, USER_ROW_MAPPER, page, sql, count, batch, market);
    }

    public int add(Connection connection, MarketRank marketRank) {
        String sql = "INSERT INTO market_ranks (batch, user_id, market, timestamp, rank, value) "
                + "VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT ON CONSTRAINT market_ranks_pk "
                + "DO UPDATE SET rank = EXCLUDED.rank, value = EXCLUDED.value "
                + "WHERE market_ranks.rank != EXCLUDED.rank OR market_ranks.value != EXCLUDED.value";
        return update(connection, INSERT_ROW_SETTER, sql, marketRank);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM market_ranks WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM market_ranks");
    }
}
