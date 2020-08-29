package vstocks.db.portfolio;

import vstocks.model.portfolio.PortfolioValue;

import java.util.Optional;

public interface PortfolioValueService {
    Optional<PortfolioValue> getForUser(String userId);
}
