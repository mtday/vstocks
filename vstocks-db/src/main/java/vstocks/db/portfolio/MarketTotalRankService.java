package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketTotalRank;
import vstocks.model.portfolio.MarketTotalRankCollection;
import vstocks.model.portfolio.RankedUser;

import java.time.Instant;
import java.util.List;

public interface MarketTotalRankService {
    long setCurrentBatch(long batch);

    int generate();

    MarketTotalRankCollection getLatest(String userId);

    Results<MarketTotalRank> getAll(Page page, List<Sort> sort);

    Results<RankedUser> getUsers(Page page);

    int add(MarketTotalRank marketTotalRank);

    int ageOff(Instant cutoff);

    int truncate();
}
