package vstocks.db.system;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.OverallTotalValue;
import vstocks.model.system.OverallTotalValueCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

public class OverallTotalValueServiceImpl extends BaseService implements OverallTotalValueService {
    private final OverallTotalValueDB overallTotalValueDB = new OverallTotalValueDB();

    public OverallTotalValueServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate() {
        return withConnection(overallTotalValueDB::generate);
    }

    @Override
    public OverallTotalValueCollection getLatest() {
        return withConnection(overallTotalValueDB::getLatest);
    }

    @Override
    public Results<OverallTotalValue> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> overallTotalValueDB.getAll(conn, page, sort));
    }

    @Override
    public int add(OverallTotalValue overallTotals) {
        return withConnection(conn -> overallTotalValueDB.add(conn, overallTotals));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> overallTotalValueDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(overallTotalValueDB::truncate);
    }
}
