package vstocks.db.system;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.OverallMarketTotalValue;
import vstocks.model.system.OverallMarketTotalValueCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

public class OverallMarketTotalValueServiceImpl extends BaseService implements OverallMarketTotalValueService {
    private final OverallMarketTotalValueDB overallMarketTotalValueDB = new OverallMarketTotalValueDB();

    public OverallMarketTotalValueServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate() {
        return withConnection(overallMarketTotalValueDB::generate);
    }

    @Override
    public OverallMarketTotalValueCollection getLatest() {
        return withConnection(overallMarketTotalValueDB::getLatest);
    }

    @Override
    public Results<OverallMarketTotalValue> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> overallMarketTotalValueDB.getAll(conn, page, sort));
    }

    @Override
    public int add(OverallMarketTotalValue overallMarketTotals) {
        return withConnection(conn -> overallMarketTotalValueDB.add(conn, overallMarketTotals));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> overallMarketTotalValueDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(overallMarketTotalValueDB::truncate);
    }
}
