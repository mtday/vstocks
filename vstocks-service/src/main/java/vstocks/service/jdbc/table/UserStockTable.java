package vstocks.service.jdbc.table;

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
                    .setMarketId(rs.getString("market_id"))
                    .setStockId(rs.getString("stock_id"))
                    .setShares(rs.getInt("shares"));

    private static final RowSetter<UserStock> INSERT_ROW_SETTER = (ps, userStock) -> {
        int index = 0;
        ps.setString(++index, userStock.getUserId());
        ps.setString(++index, userStock.getMarketId());
        ps.setString(++index, userStock.getStockId());
        ps.setInt(++index, userStock.getShares());
    };

    public Optional<UserStock> get(Connection connection, String userId, String marketId, String stockId) {
        String sql = "SELECT * FROM user_stocks WHERE user_id = ? AND market_id = ? AND stock_id = ?";
        return getOne(connection, ROW_MAPPER, sql, userId, marketId, stockId);
    }

    public Results<UserStock> getForUser(Connection connection, String userId, Page page) {
        String query = "SELECT * FROM user_stocks WHERE user_id = ? ORDER BY shares DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM user_stocks WHERE user_id = ?";
        return results(connection, ROW_MAPPER, page, query, countQuery, userId);
    }

    public Results<UserStock> getForStock(Connection connection, String stockId, Page page) {
        String query = "SELECT * FROM user_stocks WHERE stock_id = ? ORDER BY shares DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM user_stocks WHERE stock_id = ?";
        return results(connection, ROW_MAPPER, page, query, countQuery, stockId);
    }

    public Results<UserStock> getAll(Connection connection, Page page) {
        String query = "SELECT * FROM user_stocks ORDER BY user_id, market_id, stock_id, shares DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM user_stocks";
        return results(connection, ROW_MAPPER, page, query, countQuery);
    }

    public int consume(Connection connection, Consumer<UserStock> consumer) {
        String sql = "SELECT * FROM user_stocks ORDER BY user_id, market_id, stock_id, shares DESC";
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int add(Connection connection, UserStock userStock) {
        String sql = "INSERT INTO user_stocks (user_id, market_id, stock_id, shares) VALUES (?, ?, ?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, userStock);
    }

    public int update(Connection connection, String userId, String marketId, String stockId, int delta) {
        // Need to do updates instead of inserts.
        if (delta > 0) {
            // delta > 0 means buying the stock
            String update = "INSERT INTO user_stocks (user_id, market_id, stock_id, shares) VALUES (?, ?, ?, ?) "
                    + "ON CONFLICT ON CONSTRAINT user_stocks_pk DO UPDATE SET shares = user_stocks.shares + EXCLUDED.shares";
            return update(connection, update, userId, marketId, stockId, delta);
        } else if (delta < 0) {
            // delta < 0 means selling the stock
            // Don't let the number of shares go less than 0. Safe to do an UPDATE here since a row needs to exist
            // (the user needs to own the stock) before being able to sell.
            String update = "UPDATE user_stocks SET shares = shares + ? "
                    + "WHERE user_id = ? AND market_id = ? AND stock_id = ? AND shares >= ?";
            if (update(connection, update, delta, userId, marketId, stockId, Math.abs(delta)) > 0) {
                // If the number of shares dropped to 0, delete the row.
                String delete = "DELETE FROM user_stocks WHERE user_id = ? AND market_id = ? AND stock_id = ? AND shares = 0";
                update(connection, delete, userId, marketId, stockId);
                return 1;
            }
            return 0;
        }
        return 0;
    }

    public int delete(Connection connection, String userId, String marketId, String stockId) {
        String sql = "DELETE FROM user_stocks WHERE user_id = ? AND market_id = ? AND stock_id = ?";
        return update(connection, sql, userId, marketId, stockId);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM user_stocks");
    }
}
