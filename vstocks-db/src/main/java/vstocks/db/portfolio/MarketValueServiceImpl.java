package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketValue;
import vstocks.model.portfolio.MarketValueCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

public class MarketValueServiceImpl extends BaseService implements MarketValueService {
    private final MarketValueDB marketValueTable = new MarketValueDB();

    public MarketValueServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate(Market market) {
        return withConnection(conn -> marketValueTable.generate(conn, market));
    }

    @Override
    public MarketValueCollection getLatest(String userId, Market market) {
        return withConnection(conn -> marketValueTable.getLatest(conn, userId, market));
    }

    @Override
    public Map<Market, MarketValueCollection> getLatest(String userId) {
        return withConnection(conn -> marketValueTable.getLatest(conn, userId));
    }

    @Override
    public Results<MarketValue> getAll(Market market, Page page, Set<Sort> sort) {
        return withConnection(conn -> marketValueTable.getAll(conn, market, page, sort));
    }

    @Override
    public int add(MarketValue marketValue) {
        return withConnection(conn -> marketValueTable.add(conn, marketValue));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> marketValueTable.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(marketValueTable::truncate);
    }
}
