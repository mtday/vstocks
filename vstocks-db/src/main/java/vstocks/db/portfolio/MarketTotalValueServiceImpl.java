package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketTotalValue;
import vstocks.model.portfolio.MarketTotalValueCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Set;

public class MarketTotalValueServiceImpl extends BaseService implements MarketTotalValueService {
    private final MarketTotalValueDB marketTotalValueTable = new MarketTotalValueDB();

    public MarketTotalValueServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate() {
        return withConnection(marketTotalValueTable::generate);
    }

    @Override
    public MarketTotalValueCollection getLatest(String userId) {
        return withConnection(conn -> marketTotalValueTable.getLatest(conn, userId));
    }

    @Override
    public Results<MarketTotalValue> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> marketTotalValueTable.getAll(conn, page, sort));
    }

    @Override
    public int add(MarketTotalValue marketTotalValue) {
        return withConnection(conn -> marketTotalValueTable.add(conn, marketTotalValue));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> marketTotalValueTable.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(marketTotalValueTable::truncate);
    }
}
