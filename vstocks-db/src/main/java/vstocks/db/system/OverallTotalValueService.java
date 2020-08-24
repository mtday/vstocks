package vstocks.db.system;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.OverallTotalValue;
import vstocks.model.system.OverallTotalValueCollection;

import java.time.Instant;
import java.util.List;

public interface OverallTotalValueService {
    int generate();

    OverallTotalValueCollection getLatest();

    Results<OverallTotalValue> getAll(Page page, List<Sort> sort);

    int add(OverallTotalValue overallTotalValue);

    int ageOff(Instant cutoff);

    int truncate();
}
