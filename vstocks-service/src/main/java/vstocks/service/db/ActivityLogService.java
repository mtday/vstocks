package vstocks.service.db;

import vstocks.model.*;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface ActivityLogService {
    Optional<ActivityLog> get(String id);

    Results<ActivityLog> getForUser(String userId, Page page, Set<Sort> sort);

    Results<ActivityLog> getForStock(Market market, String symbol, Page page, Set<Sort> sort);

    Results<ActivityLog> getAll(Page page, Set<Sort> sort);

    int consume(Consumer<ActivityLog> consumer, Set<Sort> sort);

    int add(ActivityLog activityLog);

    int delete(String id);
}
