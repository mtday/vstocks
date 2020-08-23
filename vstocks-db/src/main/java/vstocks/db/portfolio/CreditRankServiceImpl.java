package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.CreditRank;
import vstocks.model.portfolio.CreditRankCollection;
import vstocks.model.portfolio.RankedUser;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

public class CreditRankServiceImpl extends BaseService implements CreditRankService {
    private final CreditRankDB creditRankDB = new CreditRankDB();

    public CreditRankServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long setCurrentBatch(long batch) {
        return withConnection(conn -> creditRankDB.setCurrentBatch(conn, batch));
    }

    @Override
    public int generate() {
        return withConnection(creditRankDB::generate);
    }

    @Override
    public CreditRankCollection getLatest(String userId) {
        return withConnection(conn -> creditRankDB.getLatest(conn, userId));
    }

    @Override
    public Results<CreditRank> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> creditRankDB.getAll(conn, page, sort));
    }

    @Override
    public Results<RankedUser> getUsers(Page page) {
        return withConnection(conn -> creditRankDB.getUsers(conn, page));
    }

    @Override
    public int add(CreditRank creditRank) {
        return withConnection(conn -> creditRankDB.add(conn, creditRank));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> creditRankDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(creditRankDB::truncate);
    }
}
