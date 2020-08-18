package vstocks.db.jdbc;

import vstocks.db.PortfolioValueSummaryDB;
import vstocks.db.jdbc.table.PortfolioValueSummaryTable;
import vstocks.model.*;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Set;

public class JdbcPortfolioValueSummaryDB extends BaseService implements PortfolioValueSummaryDB {
    private final PortfolioValueSummaryTable portfolioValueSummaryTable = new PortfolioValueSummaryTable();

    public JdbcPortfolioValueSummaryDB(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public PortfolioValueSummary generate() {
        return withConnection(portfolioValueSummaryTable::generate);
    }

    @Override
    public PortfolioValueSummary getLatest() {
        return withConnection(portfolioValueSummaryTable::getLatest);
    }

    @Override
    public Results<PortfolioValueSummary> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> portfolioValueSummaryTable.getAll(conn, page, sort));
    }

    @Override
    public int add(PortfolioValueSummary portfolioValueSummary) {
        return withConnection(conn -> portfolioValueSummaryTable.add(conn, portfolioValueSummary));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> portfolioValueSummaryTable.ageOff(conn, cutoff));
    }
}
