package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.TotalValue;
import vstocks.model.portfolio.TotalValueCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class TotalValueServiceImpl extends BaseService implements TotalValueService {
    private final TotalValueDB totalValueTable = new TotalValueDB();

    public TotalValueServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate(Consumer<TotalValue> consumer) {
        return withConnection(conn -> totalValueTable.generate(conn, consumer));
    }

    @Override
    public TotalValueCollection getLatest(String userId) {
        return withConnection(conn -> totalValueTable.getLatest(conn, userId));
    }

    @Override
    public Results<TotalValue> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> totalValueTable.getAll(conn, page, sort));
    }

    @Override
    public int add(TotalValue totalValue) {
        return withConnection(conn -> totalValueTable.add(conn, totalValue));
    }

    @Override
    public int addAll(Collection<TotalValue> totalValues) {
        return withConnection(conn -> totalValueTable.addAll(conn, totalValues));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> totalValueTable.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(totalValueTable::truncate);
    }
}
