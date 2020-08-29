package vstocks.db.portfolio;

import vstocks.model.portfolio.PortfolioValueSummary;

import java.util.Optional;

public interface PortfolioValueSummaryService {
    Optional<PortfolioValueSummary> getForUser(String userId);
}
