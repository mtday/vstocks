package vstocks.db;

import vstocks.model.PortfolioValue;

import java.util.Optional;
import java.util.function.Consumer;

public interface PortfolioValueDB {
    Optional<PortfolioValue> get(String userId);

    int consume(Consumer<PortfolioValue> consumer);
}
