package vstocks.db.jdbc.table;

import vstocks.model.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.SortDirection.DESC;

public class PortfolioValueRankTable extends BaseTable {
    private static final RowMapper<PortfolioValueRank> ROW_MAPPER = rs ->
            new PortfolioValueRank()
                    .setUserId(rs.getString("user_id"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant())
                    .setRank(rs.getInt("rank"));

    private static final RowSetter<PortfolioValueRank> INSERT_ROW_SETTER = (ps, portfolioValueRank) -> {
        int index = 0;
        ps.setString(++index, portfolioValueRank.getUserId());
        ps.setTimestamp(++index, Timestamp.from(portfolioValueRank.getTimestamp()));
        ps.setInt(++index, portfolioValueRank.getRank());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new LinkedHashSet<>(asList(USER_ID.toSort(), TIMESTAMP.toSort(DESC)));
    }

    private void populateDeltas(Connection connection, List<PortfolioValueRank> portfolioValueRanks) {
        if (portfolioValueRanks.isEmpty()) {
            return;
        }

        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();
        Set<String> userIds = portfolioValueRanks.stream().map(PortfolioValueRank::getUserId).collect(toSet());

        Map<String, List<PortfolioValueRank>> values = new HashMap<>();
        Consumer<PortfolioValueRank> consumer = portfolioValueRank ->
                values.computeIfAbsent(portfolioValueRank.getUserId(), userId -> new ArrayList<>()).add(portfolioValueRank);

        String sql = "SELECT * FROM portfolio_value_ranks "
                + "WHERE timestamp >= ? AND user_id = ANY(?) "
                + "ORDER BY user_id, timestamp DESC";
        consume(connection, ROW_MAPPER, consumer, sql, earliest, userIds);

        portfolioValueRanks.forEach(portfolioValueRank -> {
            List<PortfolioValueRank> pv = Stream.of(values.get(portfolioValueRank.getUserId()))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseGet(Collections::emptyList);
            // We stick a "-" on the rank since lower ranks should be seen as positive deltas
            portfolioValueRank.setDeltas(Delta.getDeltas(pv, PortfolioValueRank::getTimestamp, r -> -(long) r.getRank()));
        });
    }

    public Optional<PortfolioValueRank> getLatest(Connection connection, String userId) {
        String sql = "SELECT * FROM portfolio_value_ranks WHERE user_id = ? ORDER BY timestamp DESC LIMIT 1";
        return getOne(connection, ROW_MAPPER, sql, userId).map(portfolioValueRank -> {
            populateDeltas(connection, singletonList(portfolioValueRank));
            return portfolioValueRank;
        });
    }

    public Results<PortfolioValueRank> getLatest(Connection connection, Collection<String> userIds, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (user_id) * FROM portfolio_value_ranks WHERE user_id = ANY(?) "
                + "ORDER BY user_id, timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM (SELECT DISTINCT ON (user_id) * FROM portfolio_value_ranks "
                + "WHERE user_id = ANY(?)) AS data";
        Results<PortfolioValueRank> portfolioValueRankResults = results(connection, ROW_MAPPER, page, sql, count, userIds);
        populateDeltas(connection, portfolioValueRankResults.getResults());
        return portfolioValueRankResults;
    }

    public Results<PortfolioValueRank> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM portfolio_value_ranks %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM portfolio_value_ranks";
        Results<PortfolioValueRank> portfolioValueRankResults = results(connection, ROW_MAPPER, page, sql, count);
        populateDeltas(connection, portfolioValueRankResults.getResults());
        return portfolioValueRankResults;
    }

    public int consume(Connection connection, Consumer<PortfolioValueRank> consumer, Set<Sort> sort) {
        String sql = format("SELECT * FROM portfolio_value_ranks %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int add(Connection connection, PortfolioValueRank portfolioValueRank) {
        return addAll(connection, singleton(portfolioValueRank));
    }

    public int addAll(Connection connection, Collection<PortfolioValueRank> portfolioValueRanks) {
        String sql = "INSERT INTO portfolio_value_ranks (user_id, timestamp, rank) VALUES (?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT portfolio_value_ranks_pk DO UPDATE "
                + "SET rank = EXCLUDED.rank WHERE portfolio_value_ranks.rank != EXCLUDED.rank";
        return updateBatch(connection, INSERT_ROW_SETTER, sql, portfolioValueRanks);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM portfolio_value_ranks WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM portfolio_value_ranks");
    }
}
