package vstocks.db.system;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.ActiveUserCount;
import vstocks.model.system.ActiveUserCountCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Set;

public class ActiveUserCountServiceImpl extends BaseService implements ActiveUserCountService {
    private final ActiveUserCountDB activeUserCountTable = new ActiveUserCountDB();

    public ActiveUserCountServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate() {
        return withConnection(activeUserCountTable::generate);
    }

    @Override
    public ActiveUserCountCollection getLatest() {
        return withConnection(activeUserCountTable::getLatest);
    }

    @Override
    public Results<ActiveUserCount> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> activeUserCountTable.getAll(conn, page, sort));
    }

    @Override
    public int add(ActiveUserCount activeUserCount) {
        return withConnection(conn -> activeUserCountTable.add(conn, activeUserCount));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> activeUserCountTable.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(activeUserCountTable::truncate);
    }
}
