package vstocks.db.portfolio;

import vstocks.db.BaseService;
import vstocks.model.portfolio.PortfolioValue;

import javax.sql.DataSource;
import java.util.Optional;

public class PortfolioValueServiceImpl extends BaseService implements PortfolioValueService {
    private final PortfolioValueDB portfolioValueDB = new PortfolioValueDB();

    public PortfolioValueServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<PortfolioValue> getForUser(String userId) {
        return withConnection(conn -> portfolioValueDB.getForUser(conn, userId));
    }
}
