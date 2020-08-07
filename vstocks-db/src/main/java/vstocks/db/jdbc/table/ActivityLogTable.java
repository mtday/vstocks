package vstocks.db.jdbc.table;

import vstocks.model.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Sort.SortDirection.DESC;

public class ActivityLogTable extends BaseTable {
    private static final RowMapper<ActivityLog> ROW_MAPPER = rs ->
            new ActivityLog()
                    .setId(rs.getString("id"))
                    .setUserId(rs.getString("user_id"))
                    .setMarket(Market.valueOf(rs.getString("market")))
                    .setSymbol(rs.getString("symbol"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant())
                    .setShares(rs.getInt("shares"))
                    .setPrice(rs.getInt("price"));

    private static final RowSetter<ActivityLog> INSERT_ROW_SETTER = (ps, activityLog) -> {
        int index = 0;
        ps.setString(++index, activityLog.getId());
        ps.setString(++index, activityLog.getUserId());
        ps.setString(++index, activityLog.getMarket().name());
        ps.setString(++index, activityLog.getSymbol());
        ps.setTimestamp(++index, Timestamp.from(activityLog.getTimestamp()));
        ps.setInt(++index, activityLog.getShares());
        ps.setInt(++index, activityLog.getPrice());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new HashSet<>(asList(TIMESTAMP.toSort(DESC), USER_ID.toSort(), MARKET.toSort(), SYMBOL.toSort()));
    }

    public Optional<ActivityLog> get(Connection connection, String id) {
        return getOne(connection, ROW_MAPPER, "SELECT * FROM activity_logs WHERE id = ?", id);
    }

    public Results<ActivityLog> getForUser(Connection connection, String userId, Page page, Set<Sort> sort) {
        String query = format("SELECT * FROM activity_logs WHERE user_id = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String countQuery = "SELECT COUNT(*) FROM activity_logs WHERE user_id = ?";
        return results(connection, ROW_MAPPER, page, query, countQuery, userId);
    }

    public Results<ActivityLog> getForStock(Connection connection, Market market, String symbol, Page page, Set<Sort> sort) {
        String query = format("SELECT * FROM activity_logs WHERE market = ? AND symbol = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String countQuery = "SELECT COUNT(*) FROM activity_logs WHERE market = ? AND symbol = ?";
        return results(connection, ROW_MAPPER, page, query, countQuery, market, symbol);
    }

    public Results<ActivityLog> getAll(Connection connection, Page page, Set<Sort> sort) {
        String query = format("SELECT * FROM activity_logs %s LIMIT ? OFFSET ?", getSort(sort));
        String countQuery = "SELECT COUNT(*) FROM activity_logs";
        return results(connection, ROW_MAPPER, page, query, countQuery);
    }

    public int consume(Connection connection, Consumer<ActivityLog> consumer, Set<Sort> sort) {
        String sql = format("SELECT * FROM activity_logs %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int add(Connection connection, ActivityLog activityLog) {
        String sql = "INSERT INTO activity_logs (id, user_id, market, symbol, timestamp, shares, price) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, activityLog);
    }

    public int deleteForUser(Connection connection, String userId) {
        return update(connection, "DELETE FROM activity_logs WHERE user_id = ?", userId);
    }

    public int delete(Connection connection, String id) {
        return update(connection, "DELETE FROM activity_logs WHERE id = ?", id);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM activity_logs");
    }
}
