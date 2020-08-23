package vstocks.db.system;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.TotalTransactionCount;
import vstocks.model.system.TotalTransactionCountCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Set;

public class TotalTransactionCountServiceImpl extends BaseService implements TotalTransactionCountService {
    private final TotalTransactionCountDB totalTransactionCountTable = new TotalTransactionCountDB();

    public TotalTransactionCountServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate() {
        return withConnection(totalTransactionCountTable::generate);
    }

    @Override
    public TotalTransactionCountCollection getLatest() {
        return withConnection(totalTransactionCountTable::getLatest);
    }

    @Override
    public Results<TotalTransactionCount> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> totalTransactionCountTable.getAll(conn, page, sort));
    }

    @Override
    public int add(TotalTransactionCount totalTransactionCount) {
        return withConnection(conn -> totalTransactionCountTable.add(conn, totalTransactionCount));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> totalTransactionCountTable.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(totalTransactionCountTable::truncate);
    }
}
