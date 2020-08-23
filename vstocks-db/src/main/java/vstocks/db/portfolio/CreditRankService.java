package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.CreditRank;
import vstocks.model.portfolio.CreditRankCollection;
import vstocks.model.portfolio.RankedUser;

import java.time.Instant;
import java.util.List;

public interface CreditRankService {
    long setCurrentBatch(long batch);

    int generate();

    CreditRankCollection getLatest(String userId);

    Results<CreditRank> getAll(Page page, List<Sort> sort);

    Results<RankedUser> getUsers(Page page);

    int add(CreditRank creditRank);

    int ageOff(Instant cutoff);

    int truncate();
}
