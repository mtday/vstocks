package vstocks.db.store;

import vstocks.model.ActivityLog;
import vstocks.model.Page;
import vstocks.model.Results;

import java.sql.Connection;
import java.util.Optional;

public interface ActivityLogStore {
    Optional<ActivityLog> get(Connection connection, String id);

    Results<ActivityLog> getForUser(Connection connection, String userId, Page page);

    Results<ActivityLog> getForStock(Connection connection, String stockId, Page page);

    Results<ActivityLog> getAll(Connection connection, Page page);

    int add(Connection connection, ActivityLog activityLog);

    int delete(Connection connection, String id);

    int truncate(Connection connection);
}
