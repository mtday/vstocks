package vstocks.db.system;

import vstocks.db.BaseService;
import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.OverallMarketValue;
import vstocks.model.system.OverallMarketValueCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class OverallMarketValueServiceImpl extends BaseService implements OverallMarketValueService {
    private final OverallMarketValueDB overallMarketValueDB = new OverallMarketValueDB();

    public OverallMarketValueServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate() {
        return withConnection(overallMarketValueDB::generate);
    }

    @Override
    public OverallMarketValueCollection getLatest(Market market) {
        return withConnection(conn -> overallMarketValueDB.getLatest(conn, market));
    }

    @Override
    public Map<Market, OverallMarketValueCollection> getLatest() {
        return withConnection(overallMarketValueDB::getLatest);
    }

    @Override
    public Results<OverallMarketValue> getAll(Market market, Page page, List<Sort> sort) {
        return withConnection(conn -> overallMarketValueDB.getAll(conn, market, page, sort));
    }

    @Override
    public int add(OverallMarketValue overallMarkets) {
        return withConnection(conn -> overallMarketValueDB.add(conn, overallMarkets));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> overallMarketValueDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(overallMarketValueDB::truncate);
    }
}
