package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.TotalRank;
import vstocks.model.portfolio.TotalRankCollection;

import java.time.Instant;
import java.util.Set;

public interface TotalRankService {
    int generate();

    TotalRankCollection getLatest(String userId);

    Results<TotalRank> getAll(Page page, Set<Sort> sort);

    int add(TotalRank totalRank);

    int ageOff(Instant cutoff);

    int truncate();
}
