package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.TotalValue;
import vstocks.model.portfolio.TotalValueCollection;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public interface TotalValueService {
    int generate(Consumer<TotalValue> consumer);

    TotalValueCollection getLatest(String userId);

    Results<TotalValue> getAll(Page page, Set<Sort> sort);

    int add(TotalValue totalValue);

    int addAll(Collection<TotalValue> totalValues);

    int ageOff(Instant cutoff);

    int truncate();
}
