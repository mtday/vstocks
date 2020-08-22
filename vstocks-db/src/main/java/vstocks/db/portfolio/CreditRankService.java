package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.CreditRank;
import vstocks.model.portfolio.CreditRankCollection;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public interface CreditRankService {
    int generate(Consumer<CreditRank> consumer);

    CreditRankCollection getLatest(String userId);

    Results<CreditRank> getAll(Page page, Set<Sort> sort);

    int add(CreditRank creditRank);

    int addAll(Collection<CreditRank> creditRanks);

    int ageOff(Instant cutoff);

    int truncate();
}
