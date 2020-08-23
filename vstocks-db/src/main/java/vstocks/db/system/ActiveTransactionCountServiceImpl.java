package vstocks.db.system;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.ActiveTransactionCount;
import vstocks.model.system.ActiveTransactionCountCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

public class ActiveTransactionCountServiceImpl extends BaseService implements ActiveTransactionCountService {
    private final ActiveTransactionCountDB activeTransactionCountDB = new ActiveTransactionCountDB();

    public ActiveTransactionCountServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate() {
        return withConnection(activeTransactionCountDB::generate);
    }

    @Override
    public ActiveTransactionCountCollection getLatest() {
        return withConnection(activeTransactionCountDB::getLatest);
    }

    @Override
    public Results<ActiveTransactionCount> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> activeTransactionCountDB.getAll(conn, page, sort));
    }

    @Override
    public int add(ActiveTransactionCount activeTransactionCount) {
        return withConnection(conn -> activeTransactionCountDB.add(conn, activeTransactionCount));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> activeTransactionCountDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(activeTransactionCountDB::truncate);
    }
}
