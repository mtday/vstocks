package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketValue;
import vstocks.model.portfolio.MarketValueCollection;
import vstocks.model.portfolio.ValuedUser;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class MarketValueServiceImpl extends BaseService implements MarketValueService {
    private final MarketValueDB marketValueDB = new MarketValueDB();

    public MarketValueServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long setCurrentBatch(long batch) {
        return withConnection(conn -> marketValueDB.setCurrentBatch(conn, batch));
    }

    @Override
    public int generate() {
        return withConnection(marketValueDB::generate);
    }

    @Override
    public MarketValueCollection getLatest(String userId, Market market) {
        return withConnection(conn -> marketValueDB.getLatest(conn, userId, market));
    }

    @Override
    public Map<Market, MarketValueCollection> getLatest(String userId) {
        return withConnection(conn -> marketValueDB.getLatest(conn, userId));
    }

    @Override
    public Results<MarketValue> getAll(Market market, Page page, List<Sort> sort) {
        return withConnection(conn -> marketValueDB.getAll(conn, market, page, sort));
    }

    @Override
    public Results<ValuedUser> getUsers(Market market, Page page) {
        return withConnection(conn -> marketValueDB.getUsers(conn, market, page));
    }

    @Override
    public int add(MarketValue marketValue) {
        return withConnection(conn -> marketValueDB.add(conn, marketValue));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> marketValueDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(marketValueDB::truncate);
    }
}
