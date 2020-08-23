package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketTotalRank;
import vstocks.model.portfolio.MarketTotalRankCollection;
import vstocks.model.portfolio.RankedUser;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

public class MarketTotalRankServiceImpl extends BaseService implements MarketTotalRankService {
    private final MarketTotalRankDB marketTotalRankDB = new MarketTotalRankDB();

    public MarketTotalRankServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long setCurrentBatch(long batch) {
        return withConnection(conn -> marketTotalRankDB.setCurrentBatch(conn, batch));
    }

    @Override
    public int generate() {
        return withConnection(marketTotalRankDB::generate);
    }

    @Override
    public MarketTotalRankCollection getLatest(String userId) {
        return withConnection(conn -> marketTotalRankDB.getLatest(conn, userId));
    }

    @Override
    public Results<MarketTotalRank> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> marketTotalRankDB.getAll(conn, page, sort));
    }

    @Override
    public Results<RankedUser> getUsers(Page page) {
        return withConnection(conn -> marketTotalRankDB.getUsers(conn, page));
    }

    @Override
    public int add(MarketTotalRank marketTotalRank) {
        return withConnection(conn -> marketTotalRankDB.add(conn, marketTotalRank));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> marketTotalRankDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(marketTotalRankDB::truncate);
    }
}
