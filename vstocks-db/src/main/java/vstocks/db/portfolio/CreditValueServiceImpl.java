package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.CreditValue;
import vstocks.model.portfolio.CreditValueCollection;
import vstocks.model.portfolio.ValuedUser;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

public class CreditValueServiceImpl extends BaseService implements CreditValueService {
    private final CreditValueDB creditValueDB = new CreditValueDB();

    public CreditValueServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long setCurrentBatch(long batch) {
        return withConnection(conn -> creditValueDB.setCurrentBatch(conn, batch));
    }

    @Override
    public int generate() {
        return withConnection(creditValueDB::generate);
    }

    @Override
    public CreditValueCollection getLatest(String userId) {
        return withConnection(conn -> creditValueDB.getLatest(conn, userId));
    }

    @Override
    public Results<CreditValue> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> creditValueDB.getAll(conn, page, sort));
    }

    @Override
    public Results<ValuedUser> getUsers(Page page) {
        return withConnection(conn -> creditValueDB.getUsers(conn, page));
    }

    @Override
    public int add(CreditValue creditValue) {
        return withConnection(conn -> creditValueDB.add(conn, creditValue));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> creditValueDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(creditValueDB::truncate);
    }
}
