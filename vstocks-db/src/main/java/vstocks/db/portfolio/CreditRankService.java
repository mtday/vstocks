package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.CreditRank;
import vstocks.model.portfolio.CreditRankCollection;

import java.time.Instant;
import java.util.Set;

public interface CreditRankService {
    int generate();

    CreditRankCollection getLatest(String userId);

    Results<CreditRank> getAll(Page page, Set<Sort> sort);

    int add(CreditRank creditRank);

    int ageOff(Instant cutoff);

    int truncate();
}
