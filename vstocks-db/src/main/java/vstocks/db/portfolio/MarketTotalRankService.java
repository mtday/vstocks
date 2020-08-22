package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketTotalRank;
import vstocks.model.portfolio.MarketTotalRankCollection;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public interface MarketTotalRankService {
    int generate(Consumer<MarketTotalRank> consumer);

    MarketTotalRankCollection getLatest(String userId);

    Results<MarketTotalRank> getAll(Page page, Set<Sort> sort);

    int add(MarketTotalRank marketTotalRank);

    int addAll(Collection<MarketTotalRank> marketTotalRanks);

    int ageOff(Instant cutoff);

    int truncate();
}
