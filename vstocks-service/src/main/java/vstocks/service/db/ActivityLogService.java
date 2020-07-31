package vstocks.service.db;

import vstocks.model.ActivityLog;
import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;

import java.util.Optional;
import java.util.function.Consumer;

public interface ActivityLogService {
    Optional<ActivityLog> get(String id);

    Results<ActivityLog> getForUser(String userId, Page page);

    Results<ActivityLog> getForStock(Market market, String symbol, Page page);

    Results<ActivityLog> getAll(Page page);

    int consume(Consumer<ActivityLog> consumer);

    int add(ActivityLog activityLog);

    int delete(String id);
}
