package vstocks.db;

import vstocks.model.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface ActivityLogService {
    Optional<ActivityLog> get(String id);

    Results<ActivityLog> getForUser(String userId, Page page, List<Sort> sort);

    Results<ActivityLog> getForUser(String userId, Set<ActivityType> types, Page page, List<Sort> sort);

    Results<ActivityLog> getForUser(String userId, Market market, Set<ActivityType> types, Page page, List<Sort> sort);

    Results<ActivityLog> getForStock(Market market, String symbol, Page page, List<Sort> sort);

    Results<ActivityLog> getForType(ActivityType type, Page page, List<Sort> sort);

    Results<ActivityLog> getAll(Page page, List<Sort> sort);

    int consume(Consumer<ActivityLog> consumer, List<Sort> sort);

    <T> int consume(PreparedStatementCreator psc, RowMapper<T> rowMapper, Consumer<T> consumer);

    int add(ActivityLog activityLog);

    int deleteForUser(String userId);

    int delete(String id);

    int truncate();
}
