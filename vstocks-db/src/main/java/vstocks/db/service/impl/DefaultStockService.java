package vstocks.db.service.impl;

import vstocks.db.service.StockService;
import vstocks.db.store.StockStore;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Stock;

import javax.sql.DataSource;
import java.util.Optional;

public class DefaultStockService extends BaseService implements StockService {
    private final StockStore stockStore;

    public DefaultStockService(DataSource dataSource, StockStore stockStore) {
        super(dataSource);
        this.stockStore = stockStore;
    }

    @Override
    public Optional<Stock> get(String id) {
        return withConnection(conn -> stockStore.get(conn, id));
    }

    @Override
    public Results<Stock> getForMarket(String marketId, Page page) {
        return withConnection(conn -> stockStore.getForMarket(conn, marketId, page));
    }

    @Override
    public Results<Stock> getAll(Page page) {
        return withConnection(conn -> stockStore.getAll(conn, page));
    }

    @Override
    public int add(Stock stock) {
        return withConnection(conn -> stockStore.add(conn, stock));
    }

    @Override
    public int update(Stock stock) {
        return withConnection(conn -> stockStore.update(conn, stock));
    }

    @Override
    public int delete(String id) {
        return withConnection(conn -> stockStore.delete(conn, id));
    }
}
