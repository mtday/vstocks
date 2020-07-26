package vstocks.db.store;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;

import java.sql.Connection;
import java.util.Optional;

public interface MarketStore {
    Optional<Market> get(Connection connection, String id);

    Results<Market> getAll(Connection connection, Page page);

    int add(Connection connection, Market market);

    int update(Connection connection, Market market);

    int delete(Connection connection, String id);

    int truncate(Connection connection);
}
