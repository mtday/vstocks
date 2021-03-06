package vstocks.db;

import vstocks.model.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.sql.Types.*;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.SortDirection.DESC;

class ActivityLogDB extends BaseDB {
    private static final RowMapper<ActivityLog> ROW_MAPPER = rs -> {
        ActivityLog activityLog = new ActivityLog()
                .setId(rs.getString("id"))
                .setUserId(rs.getString("user_id"))
                .setType(ActivityType.valueOf(rs.getString("type")))
                .setTimestamp(rs.getTimestamp("timestamp").toInstant());
        ofNullable(rs.getString("market")).map(Market::valueOf).ifPresent(activityLog::setMarket);
        ofNullable(rs.getString("symbol")).ifPresent(activityLog::setSymbol);
        long shares = rs.getInt("shares");
        activityLog.setShares(rs.wasNull() ? null : shares);
        long price = rs.getLong("price");
        activityLog.setPrice(rs.wasNull() ? null : price);
        long value = rs.getLong("value");
        activityLog.setValue(rs.wasNull() ? null : value);
        return activityLog;
    };

    private static final RowSetter<ActivityLog> INSERT_ROW_SETTER = (ps, activityLog) -> {
        int index = 0;
        ps.setString(++index, activityLog.getId());
        ps.setString(++index, activityLog.getUserId());
        ps.setString(++index, activityLog.getType().name());
        ps.setTimestamp(++index, Timestamp.from(activityLog.getTimestamp()));
        if (activityLog.getMarket() != null) {
            ps.setString(++index, activityLog.getMarket().name());
        } else {
            ps.setNull(++index, VARCHAR);
        }
        if (activityLog.getSymbol() != null) {
            ps.setString(++index, activityLog.getSymbol());
        } else {
            ps.setNull(++index, VARCHAR);
        }
        if (activityLog.getShares() != null) {
            ps.setLong(++index, activityLog.getShares());
        } else {
            ps.setNull(++index, BIGINT);
        }
        if (activityLog.getPrice() != null) {
            ps.setLong(++index, activityLog.getPrice());
        } else {
            ps.setNull(++index, BIGINT);
        }
        if (activityLog.getValue() != null) {
            ps.setLong(++index, activityLog.getValue());
        } else {
            ps.setNull(++index, BIGINT);
        }
    };

    @Override
    protected List<Sort> getDefaultSort() {
        return asList(TIMESTAMP.toSort(DESC), USER_ID.toSort(), MARKET.toSort(), SYMBOL.toSort());
    }

    public Optional<ActivityLog> get(Connection connection, String id) {
        return getOne(connection, ROW_MAPPER, "SELECT * FROM activity_logs WHERE id = ?", id);
    }

    public Results<ActivityLog> getForUser(Connection connection, String userId, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM activity_logs WHERE user_id = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM activity_logs WHERE user_id = ?";
        return results(connection, ROW_MAPPER, page, sql, count, userId);
    }

    public Results<ActivityLog> getForUser(Connection connection,
                                           String userId,
                                           Set<ActivityType> types,
                                           Page page,
                                           List<Sort> sort) {
        String sql = format("SELECT * FROM activity_logs WHERE user_id = ? AND type = ANY(?) %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM activity_logs WHERE user_id = ? AND type = ANY(?)";
        return results(connection, ROW_MAPPER, page, sql, count, userId, types);
    }

    public Results<ActivityLog> getForUser(Connection connection,
                                           String userId,
                                           Market market,
                                           Set<ActivityType> types,
                                           Page page,
                                           List<Sort> sort) {
        String sql = format("SELECT * FROM activity_logs WHERE user_id = ? AND market = ? AND type = ANY(?) %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM activity_logs WHERE user_id = ? AND market = ? AND type = ANY(?)";
        return results(connection, ROW_MAPPER, page, sql, count, userId, market, types);
    }

    public Results<ActivityLog> getForStock(Connection connection, Market market, String symbol, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM activity_logs WHERE market = ? AND symbol = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM activity_logs WHERE market = ? AND symbol = ?";
        return results(connection, ROW_MAPPER, page, sql, count, market, symbol);
    }

    public Results<ActivityLog> getForType(Connection connection, ActivityType type, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM activity_logs WHERE type = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM activity_logs WHERE type = ?";
        return results(connection, ROW_MAPPER, page, sql, count, type);
    }

    public Results<ActivityLog> getAll(Connection connection, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM activity_logs %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM activity_logs";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int consume(Connection connection, Consumer<ActivityLog> consumer, List<Sort> sort) {
        String sql = format("SELECT * FROM activity_logs %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public <T> int consume(Connection connection, PreparedStatementCreator psc, RowMapper<T> rowMapper, Consumer<T> consumer) {
        return super.consume(connection, psc, rowMapper, consumer);
    }

    public int add(Connection connection, ActivityLog activityLog) {
        String sql = "INSERT INTO activity_logs (id, user_id, type, timestamp, market, symbol, shares, price, value) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
