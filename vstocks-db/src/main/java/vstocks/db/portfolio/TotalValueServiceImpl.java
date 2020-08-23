package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.TotalValue;
import vstocks.model.portfolio.TotalValueCollection;
import vstocks.model.portfolio.ValuedUser;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

public class TotalValueServiceImpl extends BaseService implements TotalValueService {
    private final TotalValueDB totalValueDB = new TotalValueDB();

    public TotalValueServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long setCurrentBatch(long batch) {
        return withConnection(conn -> totalValueDB.setCurrentBatch(conn, batch));
    }

    @Override
    public int generate() {
        return withConnection(totalValueDB::generate);
    }

    @Override
    public TotalValueCollection getLatest(String userId) {
        return withConnection(conn -> totalValueDB.getLatest(conn, userId));
    }

    @Override
    public Results<TotalValue> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> totalValueDB.getAll(conn, page, sort));
    }

    @Override
    public Results<ValuedUser> getUsers(Page page) {
        return withConnection(conn -> totalValueDB.getUsers(conn, page));
    }

    @Override
    public int add(TotalValue totalValue) {
        return withConnection(conn -> totalValueDB.add(conn, totalValue));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> totalValueDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(totalValueDB::truncate);
    }
}
