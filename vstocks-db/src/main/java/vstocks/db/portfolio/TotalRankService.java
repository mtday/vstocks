package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.TotalRank;
import vstocks.model.portfolio.TotalRankCollection;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public interface TotalRankService {
    int generate(Consumer<TotalRank> consumer);

    TotalRankCollection getLatest(String userId);

    Results<TotalRank> getAll(Page page, Set<Sort> sort);

    int add(TotalRank totalRank);

    int addAll(Collection<TotalRank> totalRanks);

    int ageOff(Instant cutoff);

    int truncate();
}
