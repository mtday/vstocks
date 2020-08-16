package vstocks.db.jdbc.table;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.UserCredits;

import java.sql.Connection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.CREDITS;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.SortDirection.DESC;

public class UserCreditsTable extends BaseTable {
    private static final RowMapper<UserCredits> ROW_MAPPER = rs ->
            new UserCredits()
                    .setUserId(rs.getString("user_id"))
                    .setCredits(rs.getLong("credits"));

    private static final RowSetter<UserCredits> INSERT_ROW_SETTER = (ps, userCredits) -> {
        int index = 0;
        ps.setString(++index, userCredits.getUserId());
        ps.setLong(++index, userCredits.getCredits());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new LinkedHashSet<>(asList(CREDITS.toSort(DESC), USER_ID.toSort()));
    }

    public Optional<UserCredits> get(Connection connection, String userId) {
        return getOne(connection, ROW_MAPPER, "SELECT * FROM user_credits WHERE user_id = ?", userId);
    }

    public Results<UserCredits> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM user_credits %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM user_credits";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int consume(Connection connection, Consumer<UserCredits> consumer, Set<Sort> sort) {
        String sql = format("SELECT * FROM user_credits %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int setInitialCredits(Connection connection, UserCredits initialCredits) {
        String sql = "INSERT INTO user_credits (user_id, credits) VALUES (?, ?) "
                + "ON CONFLICT ON CONSTRAINT user_credits_pk DO NOTHING";
        return update(connection, sql, initialCredits.getUserId(), initialCredits.getCredits());
    }

    public int add(Connection connection, UserCredits userCredits) {
        String sql = "INSERT INTO user_credits (user_id, credits) VALUES (?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, userCredits);
    }

    public int update(Connection connection, String userId, int delta) {
        if (delta > 0) {
            // Adding to the credits due to a stock sale.
            return update(connection, "UPDATE user_credits SET credits = credits + ? WHERE user_id = ?", delta, userId);
        } else if (delta < 0) {
            // Subtracting from the credits due to a stock purchase.
            // Don't let the credits go less than 0.
            String sql = "UPDATE user_credits SET credits = credits + ? WHERE user_id = ? AND credits >= ?";
            return update(connection, sql, delta, userId, Math.abs(delta));
        }
        return 0;
    }

    public int delete(Connection connection, String userId) {
        return update(connection, "DELETE FROM user_credits WHERE user_id = ?", userId);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM user_credits");
    }
}
