package vstocks.db.portfolio;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketRank;
import vstocks.model.portfolio.MarketRankCollection;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface MarketRankService {
    int generate(Market market, Consumer<MarketRank> consumer);

    MarketRankCollection getLatest(String userId, Market market);

    Map<Market, MarketRankCollection> getLatest(String userId);

    Results<MarketRank> getAll(Market market, Page page, Set<Sort> sort);

    int add(MarketRank marketRank);

    int addAll(Collection<MarketRank> marketRanks);

    int ageOff(Instant cutoff);

    int truncate();
}
