package vstocks.db.system;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.ActiveUserCount;
import vstocks.model.system.ActiveUserCountCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

public class ActiveUserCountServiceImpl extends BaseService implements ActiveUserCountService {
    private final ActiveUserCountDB activeUserCountDB = new ActiveUserCountDB();

    public ActiveUserCountServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate() {
        return withConnection(activeUserCountDB::generate);
    }

    @Override
    public ActiveUserCountCollection getLatest() {
        return withConnection(activeUserCountDB::getLatest);
    }

    @Override
    public Results<ActiveUserCount> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> activeUserCountDB.getAll(conn, page, sort));
    }

    @Override
    public int add(ActiveUserCount activeUserCount) {
        return withConnection(conn -> activeUserCountDB.add(conn, activeUserCount));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> activeUserCountDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(activeUserCountDB::truncate);
    }
}
