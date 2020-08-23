package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.RankedUser;
import vstocks.model.portfolio.TotalRank;
import vstocks.model.portfolio.TotalRankCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

public class TotalRankServiceImpl extends BaseService implements TotalRankService {
    private final TotalRankDB totalRankDB = new TotalRankDB();

    public TotalRankServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long setCurrentBatch(long batch) {
        return withConnection(conn -> totalRankDB.setCurrentBatch(conn, batch));
    }

    @Override
    public int generate() {
        return withConnection(totalRankDB::generate);
    }

    @Override
    public TotalRankCollection getLatest(String userId) {
        return withConnection(conn -> totalRankDB.getLatest(conn, userId));
    }

    @Override
    public Results<TotalRank> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> totalRankDB.getAll(conn, page, sort));
    }

    @Override
    public Results<RankedUser> getUsers(Page page) {
        return withConnection(conn -> totalRankDB.getUsers(conn, page));
    }

    @Override
    public int add(TotalRank totalRank) {
        return withConnection(conn -> totalRankDB.add(conn, totalRank));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> totalRankDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(totalRankDB::truncate);
    }
}
