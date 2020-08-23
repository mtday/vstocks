package vstocks.db.system;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.TotalUserCount;
import vstocks.model.system.TotalUserCountCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

public class TotalUserCountServiceImpl extends BaseService implements TotalUserCountService {
    private final TotalUserCountDB totalUserCountDB = new TotalUserCountDB();

    public TotalUserCountServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate() {
        return withConnection(totalUserCountDB::generate);
    }

    @Override
    public TotalUserCountCollection getLatest() {
        return withConnection(totalUserCountDB::getLatest);
    }

    @Override
    public Results<TotalUserCount> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> totalUserCountDB.getAll(conn, page, sort));
    }

    @Override
    public int add(TotalUserCount totalUserCount) {
        return withConnection(conn -> totalUserCountDB.add(conn, totalUserCount));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> totalUserCountDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(totalUserCountDB::truncate);
    }
}
