package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketTotalRank;
import vstocks.model.portfolio.MarketTotalRankCollection;

import java.time.Instant;
import java.util.Set;

public interface MarketTotalRankService {
    int generate();

    MarketTotalRankCollection getLatest(String userId);

    Results<MarketTotalRank> getAll(Page page, Set<Sort> sort);

    int add(MarketTotalRank marketTotalRank);

    int ageOff(Instant cutoff);

    int truncate();
}
