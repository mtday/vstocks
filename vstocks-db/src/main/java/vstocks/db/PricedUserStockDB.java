package vstocks.db;

import vstocks.model.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.SortDirection.DESC;

class PricedUserStockDB extends BaseDB {
    private static final RowMapper<PricedUserStock> ROW_MAPPER = rs -> {
        Timestamp timestamp = rs.getTimestamp("timestamp");
        Instant instant = rs.wasNull() ? Instant.now() : timestamp.toInstant();
        long price = rs.getLong("price");
        price = rs.wasNull() ? 1 : price;
        long shares = rs.getLong("shares");
        long value = rs.getLong("value");
        value = rs.wasNull() ? shares * price : value;
        return new PricedUserStock()
                .setUserId(rs.getString("user_id"))
                .setMarket(Market.valueOf(rs.getString("market")))
                .setSymbol(rs.getString("symbol"))
                .setName(rs.getString("name"))
                .setProfileImage(rs.getString("profile_image"))
                .setShares(rs.getInt("shares"))
                .setTimestamp(instant)
                .setPrice(price)
                .setValue(value);
    };

    @Override
    protected List<Sort> getDefaultSort() {
        return asList(USER_ID.toSort(), MARKET.toSort(), VALUE.toSort(DESC));
    }

    public Optional<PricedUserStock> get(Connection connection, String userId, Market market, String symbol) {
        String sql = "SELECT DISTINCT ON (us.market, us.symbol) us.*, s.name, s.profile_image, sp.timestamp, "
                + "sp.price, us.shares * sp.price AS value "
                + "FROM user_stocks us "
                + "LEFT JOIN stocks s ON (us.market = s.market AND us.symbol = s.symbol) "
                + "LEFT JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol) "
                + "WHERE us.user_id = ? AND us.market = ? AND us.symbol = ? "
                + "ORDER BY us.market, us.symbol, sp.timestamp DESC";
        return getOne(connection, ROW_MAPPER, sql, userId, market, symbol);
    }

    public Results<PricedUserStock> getForUser(Connection connection, String userId, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (us.market, us.symbol) us.*, s.name, s.profile_image, sp.timestamp, "
                + "sp.price, us.shares * sp.price AS value "
                + "FROM user_stocks us "
                + "LEFT JOIN stocks s ON (us.market = s.market AND us.symbol = s.symbol) "
                + "LEFT JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol) "
                + "WHERE us.user_id = ? "
                + "ORDER BY us.market, us.symbol, sp.timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM user_stocks WHERE user_id = ?";
        return results(connection, ROW_MAPPER, page, sql, count, userId);
    }

    public Results<PricedUserStock> getForUserMarket(Connection connection,
                                                     String userId,
                                                     Market market,
                                                     Page page,
                                                     List<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (us.symbol) us.*, s.name, s.profile_image, sp.timestamp, "
                + "sp.price, us.shares * sp.price AS value "
                + "FROM user_stocks us "
                + "LEFT JOIN stocks s ON (us.market = s.market AND us.symbol = s.symbol) "
                + "LEFT JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol) "
                + "WHERE us.user_id = ? AND us.market = ? "
                + "ORDER BY us.symbol, sp.timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM user_stocks WHERE user_id = ?";
        return results(connection, ROW_MAPPER, page, sql, count, userId, market);
    }

    public Results<PricedUserStock> getForStock(Connection connection,
                                                Market market,
                                                String symbol,
                                                Page page,
                                                List<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (us.user_id, us.market, us.symbol) us.*, s.name, s.profile_image, "
                + "sp.timestamp, sp.price, us.shares * sp.price AS value "
                + "FROM user_stocks us "
                + "LEFT JOIN stocks s ON (us.market = s.market AND us.symbol = s.symbol) "
                + "LEFT JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol) "
                + "WHERE us.market = ? AND us.symbol = ? "
                + "ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM user_stocks WHERE us.market = ? AND us.symbol = ?";
        return results(connection, ROW_MAPPER, page, sql, count, market, symbol);
    }

    public Results<PricedUserStock> getAll(Connection connection, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (us.user_id, us.market, us.symbol) us.*, s.name, s.profile_image, "
                + "sp.timestamp, sp.price, us.shares * sp.price AS value "
                + "FROM user_stocks us "
                + "LEFT JOIN stocks s ON (us.market = s.market AND us.symbol = s.symbol) "
                + "LEFT JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol) "
                + "ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM user_stocks";
        return results(connection, ROW_MAPPER, page, sql, count);
    }
}
