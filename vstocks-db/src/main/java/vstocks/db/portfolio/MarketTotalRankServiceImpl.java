package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketTotalRank;
import vstocks.model.portfolio.MarketTotalRankCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Set;

public class MarketTotalRankServiceImpl extends BaseService implements MarketTotalRankService {
    private final MarketTotalRankDB marketTotalRankTable = new MarketTotalRankDB();

    public MarketTotalRankServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate() {
        return withConnection(marketTotalRankTable::generate);
    }

    @Override
    public MarketTotalRankCollection getLatest(String userId) {
        return withConnection(conn -> marketTotalRankTable.getLatest(conn, userId));
    }

    @Override
    public Results<MarketTotalRank> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> marketTotalRankTable.getAll(conn, page, sort));
    }

    @Override
    public int add(MarketTotalRank marketTotalRank) {
        return withConnection(conn -> marketTotalRankTable.add(conn, marketTotalRank));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> marketTotalRankTable.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(marketTotalRankTable::truncate);
    }
}
