package vstocks.db;

import vstocks.model.*;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public interface PortfolioValueDB {
    PortfolioValue generateForUser(String userId);

    int generateAll(Consumer<PortfolioValue> consumer);

    PortfolioValueCollection getLatest(String userId);

    Results<PortfolioValue> getAll(Page page, Set<Sort> sort);

    int consume(Consumer<PortfolioValue> consumer, Set<Sort> sort);

    int add(PortfolioValue portfolioValue);

    int addAll(Collection<PortfolioValue> portfolioValues);

    int ageOff(Instant cutoff);
}
