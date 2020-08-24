package vstocks.db.system;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.OverallCreditValue;
import vstocks.model.system.OverallCreditValueCollection;

import java.time.Instant;
import java.util.List;

public interface OverallCreditValueService {
    int generate();

    OverallCreditValueCollection getLatest();

    Results<OverallCreditValue> getAll(Page page, List<Sort> sort);

    int add(OverallCreditValue overallCreditValue);

    int ageOff(Instant cutoff);

    int truncate();
}
