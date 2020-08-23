package vstocks.db;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.UserCredits;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class UserCreditsServiceImpl extends BaseService implements UserCreditsService {
    private final UserCreditsDB userCreditsDB = new UserCreditsDB();

    public UserCreditsServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<UserCredits> get(String userId) {
        return withConnection(conn -> userCreditsDB.get(conn, userId));
    }

    @Override
    public Results<UserCredits> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> userCreditsDB.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<UserCredits> consumer, List<Sort> sort) {
        return withConnection(conn -> userCreditsDB.consume(conn, consumer, sort));
    }

    @Override
    public int setInitialCredits(UserCredits initialCredits) {
        return withConnection(conn -> userCreditsDB.setInitialCredits(conn, initialCredits));
    }

    @Override
    public int add(UserCredits userCredits) {
        return withConnection(conn -> userCreditsDB.add(conn, userCredits));
    }

    @Override
    public int update(String userId, long delta) {
        return withConnection(conn -> userCreditsDB.update(conn, userId, delta));
    }

    @Override
    public int delete(String userId) {
        return withConnection(conn -> userCreditsDB.delete(conn, userId));
    }

    @Override
    public int truncate() {
        return withConnection(userCreditsDB::truncate);
    }
}
