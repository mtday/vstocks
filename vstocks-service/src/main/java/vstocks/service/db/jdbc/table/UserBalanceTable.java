package vstocks.service.db.jdbc.table;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.UserBalance;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Sort.SortDirection.DESC;

public class UserBalanceTable extends BaseTable {
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
    protected Set<Sort> getDefaultSort() {
        return new HashSet<>(asList(BALANCE.toSort(DESC), USER_ID.toSort()));
    }

    public Optional<UserBalance> get(Connection connection, String userId) {
        return getOne(connection, ROW_MAPPER, "SELECT * FROM user_balances WHERE user_id = ?", userId);
    }

    public Results<UserBalance> getAll(Connection connection, Page page, Set<Sort> sort) {
        String query = format("SELECT * FROM user_balances %s LIMIT ? OFFSET ?", getSort(sort));
        String countQuery = "SELECT COUNT(*) FROM user_balances";
        return results(connection, ROW_MAPPER, page, query, countQuery);
    }

    public int consume(Connection connection, Consumer<UserBalance> consumer, Set<Sort> sort) {
        String sql = format("SELECT * FROM user_balances %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int setInitialBalance(Connection connection, UserBalance initialBalance) {
        String sql = "INSERT INTO user_balances (user_id, balance) VALUES (?, ?) "
                + "ON CONFLICT ON CONSTRAINT user_balances_pk DO NOTHING";
        return update(connection, sql, initialBalance.getUserId(), initialBalance.getBalance());
    }

    public int add(Connection connection, UserBalance userBalance) {
        String sql = "INSERT INTO user_balances (user_id, balance) VALUES (?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, userBalance);
    }

    public int update(Connection connection, String userId, int delta) {
        if (delta > 0) {
            // Adding to the balance due to a stock sale.
            return update(connection, "UPDATE user_balances SET balance = balance + ? WHERE user_id = ?", delta, userId);
        } else if (delta < 0) {
            // Subtracting to the balance due to a stock purchase.
            // Don't let the balance go less than 0.
            String sql = "UPDATE user_balances SET balance = balance + ? WHERE user_id = ? AND balance >= ?";
            return update(connection, sql, delta, userId, Math.abs(delta));
        }
        return 0;
    }

    public int delete(Connection connection, String userId) {
        return update(connection, "DELETE FROM user_balances WHERE user_id = ?", userId);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM user_balances");
    }
}
