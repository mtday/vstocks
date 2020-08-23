package vstocks.db.system;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.TotalTransactionCount;
import vstocks.model.system.TotalTransactionCountCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

public class TotalTransactionCountServiceImpl extends BaseService implements TotalTransactionCountService {
    private final TotalTransactionCountDB totalTransactionCountDB = new TotalTransactionCountDB();

    public TotalTransactionCountServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate() {
        return withConnection(totalTransactionCountDB::generate);
    }

    @Override
    public TotalTransactionCountCollection getLatest() {
        return withConnection(totalTransactionCountDB::getLatest);
    }

    @Override
    public Results<TotalTransactionCount> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> totalTransactionCountDB.getAll(conn, page, sort));
    }

    @Override
    public int add(TotalTransactionCount totalTransactionCount) {
        return withConnection(conn -> totalTransactionCountDB.add(conn, totalTransactionCount));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> totalTransactionCountDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(totalTransactionCountDB::truncate);
    }
}
