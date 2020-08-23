package vstocks.db.system;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.ActiveTransactionCount;
import vstocks.model.system.ActiveTransactionCountCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Set;

public class ActiveTransactionCountServiceImpl extends BaseService implements ActiveTransactionCountService {
    private final ActiveTransactionCountDB activeTransactionCountTable = new ActiveTransactionCountDB();

    public ActiveTransactionCountServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate() {
        return withConnection(activeTransactionCountTable::generate);
    }

    @Override
    public ActiveTransactionCountCollection getLatest() {
        return withConnection(activeTransactionCountTable::getLatest);
    }

    @Override
    public Results<ActiveTransactionCount> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> activeTransactionCountTable.getAll(conn, page, sort));
    }

    @Override
    public int add(ActiveTransactionCount activeTransactionCount) {
        return withConnection(conn -> activeTransactionCountTable.add(conn, activeTransactionCount));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> activeTransactionCountTable.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(activeTransactionCountTable::truncate);
    }
}
