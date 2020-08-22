package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketTotalRank;
import vstocks.model.portfolio.MarketTotalRankCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class MarketTotalRankServiceImpl extends BaseService implements MarketTotalRankService {
    private final MarketTotalRankTable marketTotalRankTable = new MarketTotalRankTable();

    public MarketTotalRankServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate(Consumer<MarketTotalRank> consumer) {
        return withConnection(conn -> marketTotalRankTable.generate(conn, consumer));
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
    public int addAll(Collection<MarketTotalRank> marketTotalRanks) {
        return withConnection(conn -> marketTotalRankTable.addAll(conn, marketTotalRanks));
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
