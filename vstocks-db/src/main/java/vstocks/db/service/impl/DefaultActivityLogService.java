package vstocks.db.service.impl;

import vstocks.db.service.ActivityLogService;
import vstocks.db.store.ActivityLogStore;
import vstocks.model.ActivityLog;
import vstocks.model.Page;
import vstocks.model.Results;

import javax.sql.DataSource;
import java.util.Optional;

public class DefaultActivityLogService extends BaseService implements ActivityLogService {
    private final ActivityLogStore activityLogStore;

    public DefaultActivityLogService(DataSource dataSource, ActivityLogStore activityLogStore) {
        super(dataSource);
        this.activityLogStore = activityLogStore;
    }

    @Override
    public Optional<ActivityLog> get(String id) {
        return withConnection(conn -> activityLogStore.get(conn, id));
    }

    @Override
    public Results<ActivityLog> getForUser(String userId, Page page) {
        return withConnection(conn -> activityLogStore.getForUser(conn, userId, page));
    }

    @Override
    public Results<ActivityLog> getForStock(String stockId, Page page) {
        return withConnection(conn -> activityLogStore.getForStock(conn, stockId, page));
    }

    @Override
    public Results<ActivityLog> getAll(Page page) {
        return withConnection(conn -> activityLogStore.getAll(conn, page));
    }

    @Override
    public int add(ActivityLog activityLog) {
        return withConnection(conn -> activityLogStore.add(conn, activityLog));
    }

    @Override
    public int delete(String id) {
        return withConnection(conn -> activityLogStore.delete(conn, id));
    }
}
