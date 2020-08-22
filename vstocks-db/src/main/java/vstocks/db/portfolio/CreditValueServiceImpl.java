package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.CreditValue;
import vstocks.model.portfolio.CreditValueCollection;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class CreditValueServiceImpl extends BaseService implements CreditValueService {
    private final CreditValueDB creditValueTable = new CreditValueDB();

    public CreditValueServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int generate(Consumer<CreditValue> consumer) {
        return withConnection(conn -> creditValueTable.generate(conn, consumer));
    }

    @Override
    public CreditValueCollection getLatest(String userId) {
        return withConnection(conn -> creditValueTable.getLatest(conn, userId));
    }

    @Override
    public Results<CreditValue> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> creditValueTable.getAll(conn, page, sort));
    }

    @Override
    public int add(CreditValue creditValue) {
        return withConnection(conn -> creditValueTable.add(conn, creditValue));
    }

    @Override
    public int addAll(Collection<CreditValue> creditValues) {
        return withConnection(conn -> creditValueTable.addAll(conn, creditValues));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> creditValueTable.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(creditValueTable::truncate);
    }
}
