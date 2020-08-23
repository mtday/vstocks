package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketTotalValue;
import vstocks.model.portfolio.MarketTotalValueCollection;
import vstocks.model.portfolio.ValuedUser;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

public class MarketTotalValueServiceImpl extends BaseService implements MarketTotalValueService {
    private final MarketTotalValueDB marketTotalValueDB = new MarketTotalValueDB();

    public MarketTotalValueServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long setCurrentBatch(long batch) {
        return withConnection(conn -> marketTotalValueDB.setCurrentBatch(conn, batch));
    }

    @Override
    public int generate() {
        return withConnection(marketTotalValueDB::generate);
    }

    @Override
    public MarketTotalValueCollection getLatest(String userId) {
        return withConnection(conn -> marketTotalValueDB.getLatest(conn, userId));
    }

    @Override
    public Results<MarketTotalValue> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> marketTotalValueDB.getAll(conn, page, sort));
    }

    @Override
    public Results<ValuedUser> getUsers(Page page) {
        return withConnection(conn -> marketTotalValueDB.getUsers(conn, page));
    }

    @Override
    public int add(MarketTotalValue marketTotalValue) {
        return withConnection(conn -> marketTotalValueDB.add(conn, marketTotalValue));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> marketTotalValueDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(marketTotalValueDB::truncate);
    }
}
