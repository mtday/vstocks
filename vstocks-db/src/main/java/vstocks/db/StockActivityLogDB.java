package vstocks.db;

import vstocks.model.*;

import java.sql.Connection;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.SortDirection.DESC;

class StockActivityLogDB extends BaseDB {
    private static final RowMapper<StockActivityLog> ROW_MAPPER = rs ->
            new StockActivityLog()
                    .setId(rs.getString("id"))
                    .setUserId(rs.getString("user_id"))
                    .setType(ActivityType.valueOf(rs.getString("type")))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant())
                    .setMarket(Market.valueOf(rs.getString("market")))
                    .setSymbol(rs.getString("symbol"))
                    .setName(rs.getString("name"))
                    .setProfileImage(rs.getString("profile_image"))
                    .setShares(rs.getLong("shares"))
                    .setPrice(rs.getLong("price"))
                    .setValue(rs.getLong("value"));

    @Override
    protected List<Sort> getDefaultSort() {
        return asList(TIMESTAMP.toSort(DESC), USER_ID.toSort(), MARKET.toSort(), NAME.toSort());
    }

    public Results<StockActivityLog> getForUser(Connection connection,
                                                String userId,
                                                Set<ActivityType> types,
                                                Page page,
                                                List<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT a.*, s.name, s.profile_image FROM activity_logs a "
                + "JOIN stocks s ON (a.market = s.market AND a.symbol = s.symbol) "
                + "WHERE a.user_id = ? AND a.type = ANY(?) LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM activity_logs WHERE user_id = ? AND type = ANY(?)";
        return results(connection, ROW_MAPPER, page, sql, count, userId, types);
    }

    public Results<StockActivityLog> getForUser(Connection connection,
                                                String userId,
                                                Market market,
                                                Set<ActivityType> types,
                                                Page page,
                                                List<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT a.*, s.name, s.profile_image FROM activity_logs a "
                + "JOIN stocks s ON (a.market = s.market AND a.symbol = s.symbol) "
                + "WHERE a.user_id = ? AND a.market = ? AND a.type = ANY(?) LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM activity_logs WHERE user_id = ? AND market = ? AND type = ANY(?)";
        return results(connection, ROW_MAPPER, page, sql, count, userId, market, types);
    }

    public Results<StockActivityLog> getForStock(Connection connection,
                                                 Market market,
                                                 String symbol,
                                                 Page page,
                                                 List<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT a.*, s.name, s.profile_image FROM activity_logs a "
                + "JOIN stocks s ON (a.market = s.market AND a.symbol = s.symbol) "
                + "WHERE a.market = ? AND a.symbol = ? LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM activity_logs WHERE market = ? AND symbol = ?";
        return results(connection, ROW_MAPPER, page, sql, count, market, symbol);
    }

    public Results<StockActivityLog> getForType(Connection connection, ActivityType type, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT a.*, s.name, s.profile_image FROM activity_logs a "
                + "JOIN stocks s ON (a.market = s.market AND a.symbol = s.symbol) "
                + "WHERE a.type = ? LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM activity_logs WHERE type = ?";
        return results(connection, ROW_MAPPER, page, sql, count, type);
    }
}
