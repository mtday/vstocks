package vstocks.db;

import vstocks.model.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class ActivityLogServiceImpl extends BaseService implements ActivityLogService {
    private final ActivityLogDB activityLogDB = new ActivityLogDB();

    public ActivityLogServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<ActivityLog> get(String id) {
        return withConnection(conn -> activityLogDB.get(conn, id));
    }

    @Override
    public Results<ActivityLog> getForUser(String userId, Page page, List<Sort> sort) {
        return withConnection(conn -> activityLogDB.getForUser(conn, userId, page, sort));
    }

    @Override
    public Results<ActivityLog> getForUser(String userId, Set<ActivityType> types, Page page, List<Sort> sort) {
        return withConnection(conn -> activityLogDB.getForUser(conn, userId, types, page, sort));
    }

    @Override
    public Results<ActivityLog> getForStock(Market market, String symbol, Page page, List<Sort> sort) {
        return withConnection(conn -> activityLogDB.getForStock(conn, market, symbol, page, sort));
    }

    @Override
    public Results<ActivityLog> getForType(ActivityType type, Page page, List<Sort> sort) {
        return withConnection(conn -> activityLogDB.getForType(conn, type, page, sort));
    }

    @Override
    public Results<ActivityLog> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> activityLogDB.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<ActivityLog> consumer, List<Sort> sort) {
        return withConnection(conn -> activityLogDB.consume(conn, consumer, sort));
    }

    @Override
    public <T> int consume(PreparedStatementCreator psc, RowMapper<T> rowMapper, Consumer<T> consumer) {
        return withConnection(conn -> activityLogDB.consume(conn, psc, rowMapper, consumer));
    }

    @Override
    public int add(ActivityLog activityLog) {
        return withConnection(conn -> activityLogDB.add(conn, activityLog));
    }

    @Override
    public int deleteForUser(String userId) {
        return withConnection(conn -> activityLogDB.deleteForUser(conn, userId));
    }

    @Override
    public int delete(String id) {
        return withConnection(conn -> activityLogDB.delete(conn, id));
    }

    @Override
    public int truncate() {
        return withConnection(activityLogDB::truncate);
    }
}
