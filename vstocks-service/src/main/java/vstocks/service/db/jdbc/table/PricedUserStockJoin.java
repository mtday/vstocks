package vstocks.service.db.jdbc.table;

import vstocks.model.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;

public class PricedUserStockJoin extends BaseTable {
    private static final RowMapper<PricedUserStock> ROW_MAPPER = rs -> {
        Timestamp timestamp = rs.getTimestamp("timestamp");
        Instant instant = rs.wasNull() ? Instant.now() : timestamp.toInstant();
        int price = rs.getInt("price");
        price = rs.wasNull() ? 1 : price;
        return new PricedUserStock()
                .setUserId(rs.getString("user_id"))
                .setMarket(Market.valueOf(rs.getString("market")))
                .setSymbol(rs.getString("symbol"))
                .setShares(rs.getInt("shares"))
                .setTimestamp(instant)
                .setPrice(price);
    };

    public Optional<PricedUserStock> get(Connection connection, String userId, Market market, String symbol) {
        String sql = "SELECT DISTINCT ON (u.market, u.symbol) u.*, p.timestamp, p.price FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "WHERE u.user_id = ? AND u.market = ? AND u.symbol = ? ORDER BY u.market, u.symbol, p.timestamp DESC";
        return getOne(connection, ROW_MAPPER, sql, userId, market, symbol);
    }

    public Results<PricedUserStock> getForUser(Connection connection, String userId, Page page) {
        String query = "SELECT DISTINCT ON (u.market, u.symbol) u.*, p.timestamp, p.price FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "WHERE u.user_id = ? ORDER BY u.market, u.symbol, p.timestamp DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM ("
                + "SELECT DISTINCT ON (u.market, u.symbol) u.market, u.symbol FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "WHERE u.user_id = ? ORDER BY u.market, u.symbol, p.timestamp DESC"
                + ") AS data";
        return results(connection, ROW_MAPPER, page, query, countQuery, userId);
    }

    public Results<PricedUserStock> getForStock(Connection connection, Market market, String symbol, Page page) {
        String query = "SELECT DISTINCT ON (u.user_id, u.market, u.symbol) u.*, p.timestamp, p.price FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "WHERE u.market = ? AND u.symbol = ? "
                + "ORDER BY u.user_id, u.market, u.symbol, p.timestamp DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM ("
                + "SELECT DISTINCT ON (u.user_id, u.market, u.symbol) u.user_id FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "WHERE u.market = ? AND u.symbol = ? ORDER BY u.user_id, u.market, u.symbol, p.timestamp DESC"
                + ") AS data";
        return results(connection, ROW_MAPPER, page, query, countQuery, market, symbol);
    }

    public Results<PricedUserStock> getAll(Connection connection, Page page) {
        String query = "SELECT DISTINCT ON (u.user_id, u.market, u.symbol) u.*, p.timestamp, p.price FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "ORDER BY u.user_id, u.market, u.symbol, p.timestamp DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM ("
                + "SELECT DISTINCT ON (u.user_id, u.market, u.symbol) u.user_id, u.market, u.symbol FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "ORDER BY u.user_id, u.market, u.symbol, p.timestamp DESC"
                + ") AS data";
        return results(connection, ROW_MAPPER, page, query, countQuery);
    }

    public int consume(Connection connection, Consumer<PricedUserStock> consumer) {
        String sql = "SELECT DISTINCT ON (u.user_id, u.market, u.symbol) u.*, p.timestamp, p.price FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "ORDER BY u.user_id, u.market, u.symbol, p.timestamp DESC";
        return consume(connection, ROW_MAPPER, consumer, sql);
    }
}
