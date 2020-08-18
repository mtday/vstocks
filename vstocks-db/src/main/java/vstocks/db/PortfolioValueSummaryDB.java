package vstocks.db;

import vstocks.model.*;

import java.time.Instant;
import java.util.Set;

public interface PortfolioValueSummaryDB {
    PortfolioValueSummary generate();

    PortfolioValueSummary getLatest();

    Results<PortfolioValueSummary> getAll(Page page, Set<Sort> sort);

    int add(PortfolioValueSummary portfolioValueSummary);

    int ageOff(Instant cutoff);
}
