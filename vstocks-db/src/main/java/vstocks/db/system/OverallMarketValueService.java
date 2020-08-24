package vstocks.db.system;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.OverallMarketValue;
import vstocks.model.system.OverallMarketValueCollection;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface OverallMarketValueService {
    int generate();

    OverallMarketValueCollection getLatest(Market market);

    Map<Market, OverallMarketValueCollection> getLatest();

    Results<OverallMarketValue> getAll(Market market, Page page, List<Sort> sort);

    int add(OverallMarketValue overallMarketValue);

    int ageOff(Instant cutoff);

    int truncate();
}
