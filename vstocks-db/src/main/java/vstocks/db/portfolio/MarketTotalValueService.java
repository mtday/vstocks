package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketTotalValue;
import vstocks.model.portfolio.MarketTotalValueCollection;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public interface MarketTotalValueService {
    int generate(Consumer<MarketTotalValue> consumer);

    MarketTotalValueCollection getLatest(String userId);

    Results<MarketTotalValue> getAll(Page page, Set<Sort> sort);

    int add(MarketTotalValue marketTotalValue);

    int addAll(Collection<MarketTotalValue> marketTotalValues);

    int ageOff(Instant cutoff);

    int truncate();
}
