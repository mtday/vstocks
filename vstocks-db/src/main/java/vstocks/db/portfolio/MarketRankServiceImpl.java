package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketRank;
import vstocks.model.portfolio.MarketRankCollection;
import vstocks.model.portfolio.RankedUser;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

public class MarketRankServiceImpl extends BaseService implements MarketRankService {
    private final MarketRankDB marketRankDB = new MarketRankDB();

    public MarketRankServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long setCurrentBatch(long batch) {
        return withConnection(conn -> marketRankDB.setCurrentBatch(conn, batch));
    }

    @Override
    public int generate() {
        return withConnection(marketRankDB::generate);
    }

    @Override
    public MarketRankCollection getLatest(String userId, Market market) {
        return withConnection(conn -> marketRankDB.getLatest(conn, userId, market));
    }

    @Override
    public List<MarketRankCollection> getLatest(String userId) {
        return withConnection(conn -> marketRankDB.getLatest(conn, userId));
    }

    @Override
    public Results<MarketRank> getAll(Market market, Page page, List<Sort> sort) {
        return withConnection(conn -> marketRankDB.getAll(conn, market, page, sort));
    }

    @Override
    public Results<RankedUser> getUsers(Market market, Page page) {
        return withConnection(conn -> marketRankDB.getUsers(conn, market, page));
    }

    @Override
    public int add(MarketRank marketRank) {
        return withConnection(conn -> marketRankDB.add(conn, marketRank));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> marketRankDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(marketRankDB::truncate);
    }
}
