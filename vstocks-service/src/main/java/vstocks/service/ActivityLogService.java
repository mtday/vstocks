package vstocks.service;

import vstocks.model.ActivityLog;
import vstocks.model.Page;
import vstocks.model.Results;

import java.util.Optional;

public interface ActivityLogService {
    Optional<ActivityLog> get(String id);

    Results<ActivityLog> getForUser(String userId, Page page);

    Results<ActivityLog> getForStock(String stockId, Page page);

    Results<ActivityLog> getAll(Page page);

    int add(ActivityLog activityLog);

    int delete(String id);
}
