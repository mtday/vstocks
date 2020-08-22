package vstocks.db.system;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.TotalUserCount;
import vstocks.model.system.TotalUserCountCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Set;

public class TotalUserCountServiceImpl extends BaseService implements TotalUserCountService {
    private final TotalUserCountTable totalUserCountTable = new TotalUserCountTable();

    public TotalUserCountServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public TotalUserCount generate() {
        return withConnection(totalUserCountTable::generate);
    }

    @Override
    public TotalUserCountCollection getLatest() {
        return withConnection(totalUserCountTable::getLatest);
    }

    @Override
    public Results<TotalUserCount> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> totalUserCountTable.getAll(conn, page, sort));
    }

    @Override
    public int add(TotalUserCount totalUserCount) {
        return withConnection(conn -> totalUserCountTable.add(conn, totalUserCount));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> totalUserCountTable.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(totalUserCountTable::truncate);
    }
}
