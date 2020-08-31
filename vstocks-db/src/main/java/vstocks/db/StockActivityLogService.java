package vstocks.db;

import vstocks.model.*;

import java.util.List;
import java.util.Set;

public interface StockActivityLogService {
    Results<StockActivityLog> getForUser(String userId, Set<ActivityType> types, Page page, List<Sort> sort);

    Results<StockActivityLog> getForUser(String userId, Market market, Set<ActivityType> types, Page page, List<Sort> sort);

    Results<StockActivityLog> getForStock(Market market, String symbol, Page page, List<Sort> sort);

    Results<StockActivityLog> getForType(ActivityType type, Page page, List<Sort> sort);
}
