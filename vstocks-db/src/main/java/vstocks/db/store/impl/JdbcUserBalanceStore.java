package vstocks.db.store.impl;

import vstocks.db.store.UserBalanceStore;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.UserBalance;

import java.sql.Connection;
import java.util.Optional;

public class JdbcUserBalanceStore extends BaseStore<UserBalance> implements UserBalanceStore {
    private static final RowMapper<UserBalance> ROW_MAPPER = rs ->
            new UserBalance()
                    .setUserId(rs.getString("user_id"))
                    .setBalance(rs.getInt("balance"));

    private static final RowSetter<UserBalance> INSERT_ROW_SETTER = (ps, userBalance) -> {
        int index = 0;
        ps.setString(++index, userBalance.getUserId());
        ps.setInt(++index, userBalance.getBalance());
    };

    @Override
    public Optional<UserBalance> get(Connection connection, String userId) {
        return getOne(connection, ROW_MAPPER, "SELECT * FROM user_balances WHERE user_id = ?", userId);
    }

    @Override
    public Results<UserBalance> getAll(Connection connection, Page page) {
        String query = "SELECT * FROM user_balances ORDER BY balance DESC, user_id LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM user_balances";
        return results(connection, ROW_MAPPER, page, query, countQuery);
    }

    @Override
    public int add(Connection connection, UserBalance userBalance) {
        String sql = "INSERT INTO user_balances (user_id, balance) VALUES (?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, userBalance);
    }

    @Override
    public int update(Connection connection, String userId, int delta) {
        if (delta > 0) {
            // May want to do a MERGE here so an initial update with a positive delta adds the row when missing.
            // See JdbcUserStockStore#update for an example
            return update(connection, "UPDATE user_balances SET balance = balance + ? WHERE user_id = ?", delta, userId);
        } else if (delta < 0) {
            // Don't let the balance go less than 0.
            String sql = "UPDATE user_balances SET balance = balance + ? WHERE user_id = ? AND balance >= ?";
            return update(connection, sql, delta, userId, Math.abs(delta));
        }
        return 0;
    }

    @Override
    public int delete(Connection connection, String userId) {
        return update(connection, "DELETE FROM user_balances WHERE user_id = ?", userId);
    }

    @Override
    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM user_balances");
    }
}
