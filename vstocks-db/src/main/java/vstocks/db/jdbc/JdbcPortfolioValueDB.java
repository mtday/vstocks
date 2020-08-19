package vstocks.db.jdbc;

import vstocks.db.PortfolioValueDB;
import vstocks.db.jdbc.table.PortfolioValueTable;
import vstocks.model.*;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class JdbcPortfolioValueDB extends BaseService implements PortfolioValueDB {
    private final PortfolioValueTable portfolioValueTable = new PortfolioValueTable();

    public JdbcPortfolioValueDB(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public PortfolioValue generateForUser(String userId) {
        return withConnection(conn -> portfolioValueTable.generateForUser(conn, userId));
    }

    @Override
    public int generateAll(Consumer<PortfolioValue> consumer) {
        return withConnection(conn -> portfolioValueTable.generateAll(conn, consumer));
    }

    @Override
    public PortfolioValueCollection getLatest(String userId) {
        return withConnection(conn -> portfolioValueTable.getLatest(conn, userId));
    }

    @Override
    public Results<PortfolioValue> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> portfolioValueTable.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<PortfolioValue> consumer, Set<Sort> sort) {
        return withConnection(conn -> portfolioValueTable.consume(conn, consumer, sort));
    }

    @Override
    public int add(PortfolioValue portfolioValue) {
        return withConnection(conn -> portfolioValueTable.add(conn, portfolioValue));
    }

    @Override
    public int addAll(Collection<PortfolioValue> portfolioValues) {
        return withConnection(conn -> portfolioValueTable.addAll(conn, portfolioValues));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> portfolioValueTable.ageOff(conn, cutoff));
    }
}
