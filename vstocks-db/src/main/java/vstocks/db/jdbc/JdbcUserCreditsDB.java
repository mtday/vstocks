package vstocks.db.jdbc;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.UserCredits;
import vstocks.db.UserCreditsDB;
import vstocks.db.jdbc.table.UserCreditsTable;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class JdbcUserCreditsDB extends BaseService implements UserCreditsDB {
    private final UserCreditsTable userCreditsTable = new UserCreditsTable();

    public JdbcUserCreditsDB(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<UserCredits> get(String userId) {
        return withConnection(conn -> userCreditsTable.get(conn, userId));
    }

    @Override
    public Results<UserCredits> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> userCreditsTable.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<UserCredits> consumer, Set<Sort> sort) {
        return withConnection(conn -> userCreditsTable.consume(conn, consumer, sort));
    }

    @Override
    public int setInitialCredits(UserCredits initialCredits) {
        return withConnection(conn -> userCreditsTable.setInitialCredits(conn, initialCredits));
    }

    @Override
    public int add(UserCredits userCredits) {
        return withConnection(conn -> userCreditsTable.add(conn, userCredits));
    }

    @Override
    public int update(String userId, int delta) {
        return withConnection(conn -> userCreditsTable.update(conn, userId, delta));
    }

    @Override
    public int delete(String userId) {
        return withConnection(conn -> userCreditsTable.delete(conn, userId));
    }
}
