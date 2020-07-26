package vstocks.service;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;

import java.util.Optional;

public interface MarketService {
    Optional<Market> get(String id);

    Results<Market> getAll(Page page);

    int add(Market market);

    int update(Market market);

    int delete(String id);
}
