package vstocks.service.db.jdbc;

import vstocks.model.*;
import vstocks.service.db.ActivityLogService;
import vstocks.service.db.jdbc.table.ActivityLogTable;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Set;
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
    public Results<ActivityLog> getForUser(String userId, Page page, Set<Sort> sort) {
        return withConnection(conn -> activityLogTable.getForUser(conn, userId, page, sort));
    }

    @Override
    public Results<ActivityLog> getForStock(Market market, String symbol, Page page, Set<Sort> sort) {
        return withConnection(conn -> activityLogTable.getForStock(conn, market, symbol, page, sort));
    }

    @Override
    public Results<ActivityLog> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> activityLogTable.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<ActivityLog> consumer, Set<Sort> sort) {
        return withConnection(conn -> activityLogTable.consume(conn, consumer, sort));
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
