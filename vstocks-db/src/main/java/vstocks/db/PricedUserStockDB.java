package vstocks.db;

import vstocks.model.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.SortDirection.DESC;

class PricedUserStockDB extends BaseTable {
    private static final RowMapper<PricedUserStock> ROW_MAPPER = rs -> {
        Timestamp timestamp = rs.getTimestamp("timestamp");
        Instant instant = rs.wasNull() ? Instant.now() : timestamp.toInstant();
        long price = rs.getLong("price");
        price = rs.wasNull() ? 1 : price;
        return new PricedUserStock()
                .setUserId(rs.getString("user_id"))
                .setMarket(Market.valueOf(rs.getString("market")))
                .setSymbol(rs.getString("symbol"))
                .setShares(rs.getInt("shares"))
                .setTimestamp(instant)
                .setPrice(price);
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new LinkedHashSet<>(asList(USER_ID.toSort(), MARKET.toSort(), SYMBOL.toSort(), TIMESTAMP.toSort(DESC)));
    }

    public Optional<PricedUserStock> get(Connection connection, String userId, Market market, String symbol) {
        String sql = "SELECT DISTINCT ON (u.market, u.symbol) u.*, p.timestamp, p.price FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "WHERE u.user_id = ? AND u.market = ? AND u.symbol = ? ORDER BY u.market, u.symbol, p.timestamp DESC";
        return getOne(connection, ROW_MAPPER, sql, userId, market, symbol);
    }

    public Results<PricedUserStock> getForUser(Connection connection, String userId, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (u.market, u.symbol) u.*, p.timestamp, p.price FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "WHERE u.user_id = ? ORDER BY u.market, u.symbol, p.timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM ("
                + "SELECT DISTINCT ON (u.market, u.symbol) u.market, u.symbol FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "WHERE u.user_id = ? ORDER BY u.market, u.symbol, p.timestamp DESC"
                + ") AS data";
        return results(connection, ROW_MAPPER, page, sql, count, userId);
    }

    public Results<PricedUserStock> getForStock(Connection connection, Market market, String symbol, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (u.user_id, u.market, u.symbol) u.*, p.timestamp, p.price FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "WHERE u.market = ? AND u.symbol = ? "
                + "ORDER BY u.user_id, u.market, u.symbol, p.timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM ("
                + "SELECT DISTINCT ON (u.user_id, u.market, u.symbol) u.user_id FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "WHERE u.market = ? AND u.symbol = ? ORDER BY u.user_id, u.market, u.symbol, p.timestamp DESC"
                + ") AS data";
        return results(connection, ROW_MAPPER, page, sql, count, market, symbol);
    }

    public Results<PricedUserStock> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (u.user_id, u.market, u.symbol) u.*, p.timestamp, p.price FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "ORDER BY u.user_id, u.market, u.symbol, p.timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM ("
                + "SELECT DISTINCT ON (u.user_id, u.market, u.symbol) u.user_id, u.market, u.symbol FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "ORDER BY u.user_id, u.market, u.symbol, p.timestamp DESC"
                + ") AS data";
        return results(connection, ROW_MAPPER, page, sql, count);
    }
}
