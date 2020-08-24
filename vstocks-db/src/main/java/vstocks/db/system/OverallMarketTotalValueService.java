package vstocks.db.system;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.OverallMarketTotalValue;
import vstocks.model.system.OverallMarketTotalValueCollection;

import java.time.Instant;
import java.util.List;

public interface OverallMarketTotalValueService {
    int generate();

    OverallMarketTotalValueCollection getLatest();

    Results<OverallMarketTotalValue> getAll(Page page, List<Sort> sort);

    int add(OverallMarketTotalValue overallMarketTotalValue);

    int ageOff(Instant cutoff);

    int truncate();
}
