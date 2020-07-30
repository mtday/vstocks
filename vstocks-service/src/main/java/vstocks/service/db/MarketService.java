package vstocks.service.db;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;

import java.util.Optional;
import java.util.function.Consumer;

public interface MarketService {
    Optional<Market> get(String id);

    Results<Market> getAll(Page page);

    int consume(Consumer<Market> consumer);

    int add(Market market);

    int update(Market market);

    int delete(String id);
}
