package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.portfolio.PortfolioValueSummary;

import javax.sql.DataSource;
import java.util.Optional;

public class PortfolioValueSummaryServiceImpl extends BaseService implements PortfolioValueSummaryService {
    private final PortfolioValueSummaryDB portfolioValueSummaryDB = new PortfolioValueSummaryDB();

    public PortfolioValueSummaryServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<PortfolioValueSummary> getForUser(String userId) {
        return withConnection(conn -> portfolioValueSummaryDB.getForUser(conn, userId));
    }
}
