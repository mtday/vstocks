package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.TotalRank;
import vstocks.model.portfolio.TotalRankCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class TotalRankServiceImpl extends BaseService implements TotalRankService {
    private final TotalRankDB totalRankTable = new TotalRankDB();

    public TotalRankServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate(Consumer<TotalRank> consumer) {
        return withConnection(conn -> totalRankTable.generate(conn, consumer));
    }

    @Override
    public TotalRankCollection getLatest(String userId) {
        return withConnection(conn -> totalRankTable.getLatest(conn, userId));
    }

    @Override
    public Results<TotalRank> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> totalRankTable.getAll(conn, page, sort));
    }

    @Override
    public int add(TotalRank totalRank) {
        return withConnection(conn -> totalRankTable.add(conn, totalRank));
    }

    @Override
    public int addAll(Collection<TotalRank> totalRanks) {
        return withConnection(conn -> totalRankTable.addAll(conn, totalRanks));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> totalRankTable.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(totalRankTable::truncate);
    }
}
