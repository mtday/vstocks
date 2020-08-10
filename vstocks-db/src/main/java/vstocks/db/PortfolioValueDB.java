package vstocks.db;

import vstocks.model.*;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface PortfolioValueDB {
    Optional<PortfolioValue> generate(String userId);

    int generateAll(Consumer<PortfolioValue> consumer);

    Optional<PortfolioValue> getLatest(String userId);

    Results<PortfolioValue> getLatest(Collection<String> userIds, Page page, Set<Sort> sort);

    Results<PortfolioValue> getForUser(String userId, Page page, Set<Sort> sort);

    Results<PortfolioValue> getAll(Page page, Set<Sort> sort);

    int consume(Consumer<PortfolioValue> consumer, Set<Sort> sort);

    int add(PortfolioValue portfolioValue);

    int addAll(Collection<PortfolioValue> portfolioValues);

    int ageOff(Instant cutoff);
}
