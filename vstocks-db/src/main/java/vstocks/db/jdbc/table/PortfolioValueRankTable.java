package vstocks.db.jdbc.table;

import vstocks.model.Page;
import vstocks.model.PortfolioValueRank;
import vstocks.model.Results;
import vstocks.model.Sort;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
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

    public Optional<PortfolioValueRank> getLatest(Connection connection, String userId) {
        String sql = "SELECT * FROM portfolio_value_ranks WHERE user_id = ? ORDER BY timestamp DESC LIMIT 1";
        return getOne(connection, ROW_MAPPER, sql, userId);
    }

    public Results<PortfolioValueRank> getLatest(Connection connection, Collection<String> userIds, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (user_id) * FROM portfolio_value_ranks WHERE user_id = ANY(?) "
                + "ORDER BY user_id, timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM (SELECT DISTINCT ON (user_id) * FROM portfolio_value_ranks "
                + "WHERE user_id = ANY(?)) AS data";
        return results(connection, ROW_MAPPER, page, sql, count, userIds);
    }

    public Results<PortfolioValueRank> getForUser(Connection connection, String userId, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM portfolio_value_ranks WHERE user_id = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM portfolio_value_ranks WHERE user_id = ?";
        return results(connection, ROW_MAPPER, page, sql, count, userId);
    }

    public List<PortfolioValueRank> getForUserSince(Connection connection, String userId, Instant earliest, Set<Sort> sort) {
        String sql = format("SELECT * FROM portfolio_value_ranks WHERE user_id = ? AND timestamp >= ? %s", getSort(sort));
        return getList(connection, ROW_MAPPER, sql, userId, earliest);
    }

    public Results<PortfolioValueRank> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM portfolio_value_ranks %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM portfolio_value_ranks";
        return results(connection, ROW_MAPPER, page, sql, count);
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
