package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketRank;
import vstocks.model.portfolio.MarketRankCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class MarketRankServiceImpl extends BaseService implements MarketRankService {
    private final MarketRankDB marketRankTable = new MarketRankDB();

    public MarketRankServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate(Market market) {
        return withConnection(conn -> marketRankTable.generate(conn, market));
    }

    @Override
    public MarketRankCollection getLatest(String userId, Market market) {
        return withConnection(conn -> marketRankTable.getLatest(conn, userId, market));
    }

    @Override
    public Map<Market, MarketRankCollection> getLatest(String userId) {
        return withConnection(conn -> marketRankTable.getLatest(conn, userId));
    }

    @Override
    public Results<MarketRank> getAll(Market market, Page page, Set<Sort> sort) {
        return withConnection(conn -> marketRankTable.getAll(conn, market, page, sort));
    }

    @Override
    public int add(MarketRank marketRank) {
        return withConnection(conn -> marketRankTable.add(conn, marketRank));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> marketRankTable.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(marketRankTable::truncate);
    }
}
