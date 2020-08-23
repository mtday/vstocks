package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.RankedUser;
import vstocks.model.portfolio.TotalRank;
import vstocks.model.portfolio.TotalRankCollection;

import java.time.Instant;
import java.util.List;

public interface TotalRankService {
    long setCurrentBatch(long batch);

    int generate();

    TotalRankCollection getLatest(String userId);

    Results<TotalRank> getAll(Page page, List<Sort> sort);

    Results<RankedUser> getUsers(Page page);

    int add(TotalRank totalRank);

    int ageOff(Instant cutoff);

    int truncate();
}
