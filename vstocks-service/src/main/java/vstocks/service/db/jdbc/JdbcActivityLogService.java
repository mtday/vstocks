package vstocks.service.db.jdbc;

import vstocks.model.ActivityLog;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.service.db.ActivityLogService;
import vstocks.service.db.jdbc.table.ActivityLogTable;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.function.Consumer;

public class JdbcActivityLogService extends BaseService implements ActivityLogService {
    private final ActivityLogTable activityLogTable = new ActivityLogTable();

    public JdbcActivityLogService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<ActivityLog> get(String id) {
        return withConnection(conn -> activityLogTable.get(conn, id));
    }

    @Override
    public Results<ActivityLog> getForUser(String userId, Page page) {
        return withConnection(conn -> activityLogTable.getForUser(conn, userId, page));
    }

    @Override
    public Results<ActivityLog> getForStock(String stockId, Page page) {
        return withConnection(conn -> activityLogTable.getForStock(conn, stockId, page));
    }

    @Override
    public Results<ActivityLog> getAll(Page page) {
        return withConnection(conn -> activityLogTable.getAll(conn, page));
    }

    @Override
    public int consume(Consumer<ActivityLog> consumer) {
        return withConnection(conn -> activityLogTable.consume(conn, consumer));
    }

    @Override
    public int add(ActivityLog activityLog) {
        return withConnection(conn -> activityLogTable.add(conn, activityLog));
    }

    @Override
    public int delete(String id) {
        return withConnection(conn -> activityLogTable.delete(conn, id));
    }
}
