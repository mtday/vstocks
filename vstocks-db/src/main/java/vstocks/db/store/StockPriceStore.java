package vstocks.db.store;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.StockPrice;

import java.sql.Connection;
import java.util.Collection;
import java.util.Optional;

public interface StockPriceStore {
    Optional<StockPrice> get(Connection connection, String id);

    Optional<StockPrice> getLatest(Connection connection, String stockId);

    Results<StockPrice> getLatest(Connection connection, Collection<String> stockIds, Page page);

    Results<StockPrice> getForStock(Connection connection, String stockId, Page page);

    Results<StockPrice> getAll(Connection connection, Page page);

    int add(Connection connection, StockPrice stockPrice);

    int delete(Connection connection, String id);

    int truncate(Connection connection);
}
