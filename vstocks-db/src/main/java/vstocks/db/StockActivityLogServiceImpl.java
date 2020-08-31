package vstocks.db;

import vstocks.model.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

public class StockActivityLogServiceImpl extends BaseService implements StockActivityLogService {
    private final StockActivityLogDB stockActivityLogDB = new StockActivityLogDB();

    public StockActivityLogServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Results<StockActivityLog> getForUser(String userId, Set<ActivityType> types, Page page, List<Sort> sort) {
        return withConnection(conn -> stockActivityLogDB.getForUser(conn, userId, types, page, sort));
    }

    @Override
    public Results<StockActivityLog> getForUser(String userId, Market market, Set<ActivityType> types, Page page, List<Sort> sort) {
        return withConnection(conn -> stockActivityLogDB.getForUser(conn, userId, market, types, page, sort));
    }

    @Override
    public Results<StockActivityLog> getForStock(Market market, String symbol, Page page, List<Sort> sort) {
        return withConnection(conn -> stockActivityLogDB.getForStock(conn, market, symbol, page, sort));
    }

    @Override
    public Results<StockActivityLog> getForType(ActivityType type, Page page, List<Sort> sort) {
        return withConnection(conn -> stockActivityLogDB.getForType(conn, type, page, sort));
    }
}
