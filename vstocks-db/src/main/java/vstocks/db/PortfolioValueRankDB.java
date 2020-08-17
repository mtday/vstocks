package vstocks.db;

import vstocks.model.Page;
import vstocks.model.PortfolioValueRank;
import vstocks.model.Results;
import vstocks.model.Sort;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface PortfolioValueRankDB {
    Optional<PortfolioValueRank> getLatest(String userId);

    Results<PortfolioValueRank> getLatest(Collection<String> userIds, Page page, Set<Sort> sort);

    Results<PortfolioValueRank> getAll(Page page, Set<Sort> sort);

    int consume(Consumer<PortfolioValueRank> consumer, Set<Sort> sort);

    int add(PortfolioValueRank portfolioValueRank);

    int addAll(Collection<PortfolioValueRank> portfolioValueRanks);

    int ageOff(Instant cutoff);
}
