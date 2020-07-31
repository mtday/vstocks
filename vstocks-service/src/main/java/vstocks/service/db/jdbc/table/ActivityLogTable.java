package vstocks.service.db.jdbc.table;

import vstocks.model.ActivityLog;
import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.function.Consumer;

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

    public Optional<ActivityLog> get(Connection connection, String id) {
        return getOne(connection, ROW_MAPPER, "SELECT * FROM activity_logs WHERE id = ?", id);
    }

    public Results<ActivityLog> getForUser(Connection connection, String userId, Page page) {
        String query = "SELECT * FROM activity_logs WHERE user_id = ? ORDER BY timestamp DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM activity_logs WHERE user_id = ?";
        return results(connection, ROW_MAPPER, page, query, countQuery, userId);
    }

    public Results<ActivityLog> getForStock(Connection connection, Market market, String symbol, Page page) {
        String query = "SELECT * FROM activity_logs WHERE market = ? AND symbol = ? ORDER BY timestamp DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM activity_logs WHERE market = ? AND symbol = ?";
        return results(connection, ROW_MAPPER, page, query, countQuery, market, symbol);
    }

    public Results<ActivityLog> getAll(Connection connection, Page page) {
        String query = "SELECT * FROM activity_logs ORDER BY timestamp DESC, user_id, market, symbol LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM activity_logs";
        return results(connection, ROW_MAPPER, page, query, countQuery);
    }

    public int consume(Connection connection, Consumer<ActivityLog> consumer) {
        String sql = "SELECT * FROM activity_logs ORDER BY timestamp DESC, user_id, market, symbol";
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int add(Connection connection, ActivityLog activityLog) {
        String sql = "INSERT INTO activity_logs (id, user_id, market, symbol, timestamp, shares, price) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, activityLog);
    }

    public int delete(Connection connection, String id) {
        return update(connection, "DELETE FROM activity_logs WHERE id = ?", id);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM activity_logs");
    }
}
