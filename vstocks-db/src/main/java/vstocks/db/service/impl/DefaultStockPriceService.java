package vstocks.db.service.impl;

import vstocks.db.service.StockPriceService;
import vstocks.db.store.StockPriceStore;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.StockPrice;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Optional;

public class DefaultStockPriceService extends BaseService implements StockPriceService {
    private final StockPriceStore stockPriceStore;

    public DefaultStockPriceService(DataSource dataSource, StockPriceStore stockPriceStore) {
        super(dataSource);
        this.stockPriceStore = stockPriceStore;
    }

    @Override
    public Optional<StockPrice> get(String id) {
        return withConnection(conn -> stockPriceStore.get(conn, id));
    }

    @Override
    public Results<StockPrice> getLatest(Collection<String> stockIds, Page page) {
        return withConnection(conn -> stockPriceStore.getLatest(conn, stockIds, page));
    }

    @Override
    public Results<StockPrice> getForStock(String stockId, Page page) {
        return withConnection(conn -> stockPriceStore.getForStock(conn, stockId, page));
    }

    @Override
    public Results<StockPrice> getAll(Page page) {
        return withConnection(conn -> stockPriceStore.getAll(conn, page));
    }

    @Override
    public int add(StockPrice stockPrice) {
        return withConnection(conn -> stockPriceStore.add(conn, stockPrice));
    }

    @Override
    public int delete(String id) {
        return withConnection(conn -> stockPriceStore.delete(conn, id));
    }
}
