package vstocks.db.portfolio;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketRank;
import vstocks.model.portfolio.MarketRankCollection;
import vstocks.model.portfolio.RankedUser;

import java.time.Instant;
import java.util.List;

public interface MarketRankService {
    long setCurrentBatch(long batch);

    int generate();

    MarketRankCollection getLatest(String userId, Market market);

    List<MarketRankCollection> getLatest(String userId);

    Results<MarketRank> getAll(Market market, Page page, List<Sort> sort);

    Results<RankedUser> getUsers(Market market, Page page);

    int add(MarketRank marketRank);

    int ageOff(Instant cutoff);

    int truncate();
}
