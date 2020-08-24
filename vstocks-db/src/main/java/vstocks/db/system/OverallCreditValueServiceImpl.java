package vstocks.db.system;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.OverallCreditValue;
import vstocks.model.system.OverallCreditValueCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

public class OverallCreditValueServiceImpl extends BaseService implements OverallCreditValueService {
    private final OverallCreditValueDB overallCreditValueDB = new OverallCreditValueDB();

    public OverallCreditValueServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate() {
        return withConnection(overallCreditValueDB::generate);
    }

    @Override
    public OverallCreditValueCollection getLatest() {
        return withConnection(overallCreditValueDB::getLatest);
    }

    @Override
    public Results<OverallCreditValue> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> overallCreditValueDB.getAll(conn, page, sort));
    }

    @Override
    public int add(OverallCreditValue overallCredits) {
        return withConnection(conn -> overallCreditValueDB.add(conn, overallCredits));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> overallCreditValueDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(overallCreditValueDB::truncate);
    }
}
