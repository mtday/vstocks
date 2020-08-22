package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.CreditRank;
import vstocks.model.portfolio.CreditRankCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class CreditRankServiceImpl extends BaseService implements CreditRankService {
    private final CreditRankTable creditRankTable = new CreditRankTable();

    public CreditRankServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate(Consumer<CreditRank> consumer) {
        return withConnection(conn -> creditRankTable.generate(conn, consumer));
    }

    @Override
    public CreditRankCollection getLatest(String userId) {
        return withConnection(conn -> creditRankTable.getLatest(conn, userId));
    }

    @Override
    public Results<CreditRank> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> creditRankTable.getAll(conn, page, sort));
    }

    @Override
    public int add(CreditRank creditRank) {
        return withConnection(conn -> creditRankTable.add(conn, creditRank));
    }

    @Override
    public int addAll(Collection<CreditRank> creditRanks) {
        return withConnection(conn -> creditRankTable.addAll(conn, creditRanks));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> creditRankTable.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(creditRankTable::truncate);
    }
}
