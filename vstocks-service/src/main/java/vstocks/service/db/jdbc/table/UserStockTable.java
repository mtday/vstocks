package vstocks.service.db.jdbc.table;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.UserStock;

import java.sql.Connection;
import java.util.Optional;
import java.util.function.Consumer;

public class UserStockTable extends BaseTable {
    private static final RowMapper<UserStock> ROW_MAPPER = rs ->
            new UserStock()
                    .setUserId(rs.getString("user_id"))
                    .setMarket(Market.valueOf(rs.getString("market")))
                    .setSymbol(rs.getString("symbol"))
                    .setShares(rs.getInt("shares"));

    private static final RowSetter<UserStock> INSERT_ROW_SETTER = (ps, userStock) -> {
        int index = 0;
        ps.setString(++index, userStock.getUserId());
        ps.setString(++index, userStock.getMarket().name());
        ps.setString(++index, userStock.getSymbol());
        ps.setInt(++index, userStock.getShares());
    };

    public Optional<UserStock> get(Connection connection, String userId, Market market, String symbol) {
        String sql = "SELECT * FROM user_stocks WHERE user_id = ? AND market = ? AND symbol = ?";
        return getOne(connection, ROW_MAPPER, sql, userId, market, symbol);
    }

    public Results<UserStock> getForUser(Connection connection, String userId, Page page) {
        String query = "SELECT * FROM user_stocks WHERE user_id = ? ORDER BY shares DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM user_stocks WHERE user_id = ?";
        return results(connection, ROW_MAPPER, page, query, countQuery, userId);
    }

    public Results<UserStock> getForStock(Connection connection, Market market, String symbol, Page page) {
        String query = "SELECT * FROM user_stocks WHERE market = ? AND symbol = ? "
                + "ORDER BY shares DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM user_stocks WHERE market = ? AND symbol = ?";
        return results(connection, ROW_MAPPER, page, query, countQuery, market, symbol);
    }

    public Results<UserStock> getAll(Connection connection, Page page) {
        String query = "SELECT * FROM user_stocks ORDER BY user_id, market, symbol, shares DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM user_stocks";
        return results(connection, ROW_MAPPER, page, query, countQuery);
    }

    public int consume(Connection connection, Consumer<UserStock> consumer) {
        String sql = "SELECT * FROM user_stocks ORDER BY user_id, market, symbol, shares DESC";
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int add(Connection connection, UserStock userStock) {
        String sql = "INSERT INTO user_stocks (user_id, market, symbol, shares) VALUES (?, ?, ?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, userStock);
    }

    public int update(Connection connection, String userId, Market market, String symbol, int delta) {
        // Need to do updates instead of inserts.
        if (delta > 0) {
            // delta > 0 means buying the stock
            String update = "INSERT INTO user_stocks (user_id, market, symbol, shares) VALUES (?, ?, ?, ?) "
                    + "ON CONFLICT ON CONSTRAINT user_stocks_pk DO UPDATE SET shares = user_stocks.shares + EXCLUDED.shares";
            return update(connection, update, userId, market, symbol, delta);
        } else if (delta < 0) {
            // delta < 0 means selling the stock
            // Don't let the number of shares go less than 0. Safe to do an UPDATE here since a row needs to exist
            // (the user needs to own the stock) before being able to sell.
            String update = "UPDATE user_stocks SET shares = shares + ? "
                    + "WHERE user_id = ? AND market = ? AND symbol = ? AND shares >= ?";
            if (update(connection, update, delta, userId, market, symbol, Math.abs(delta)) > 0) {
                // If the number of shares dropped to 0, delete the row.
                String delete = "DELETE FROM user_stocks WHERE user_id = ? AND market = ? AND symbol = ? AND shares = 0";
                update(connection, delete, userId, market, symbol);
                return 1;
            }
            return 0;
        }
        return 0;
    }

    public int delete(Connection connection, String userId, Market market, String symbol) {
        String sql = "DELETE FROM user_stocks WHERE user_id = ? AND market = ? AND symbol = ?";
        return update(connection, sql, userId, market, symbol);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM user_stocks");
    }
}
