package vstocks.db;

import vstocks.model.*;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public interface PortfolioValueRankDB {
    PortfolioValueRankCollection getLatest(String userId);

    Results<PortfolioValueRank> getAll(Page page, Set<Sort> sort);

    int consume(Consumer<PortfolioValueRank> consumer, Set<Sort> sort);

    int add(PortfolioValueRank portfolioValueRank);

    int addAll(Collection<PortfolioValueRank> portfolioValueRanks);

    int ageOff(Instant cutoff);
}
