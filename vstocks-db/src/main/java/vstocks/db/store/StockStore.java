package vstocks.db.store;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Stock;

import java.sql.Connection;
import java.util.Optional;

public interface StockStore {
    Optional<Stock> get(Connection connection, String id);

    Results<Stock> getForMarket(Connection connection, String marketId, Page page);

    Results<Stock> getAll(Connection connection, Page page);

    int add(Connection connection, Stock stock);

    int update(Connection connection, Stock stock);

    int delete(Connection connection, String id);

    int truncate(Connection connection);
}
