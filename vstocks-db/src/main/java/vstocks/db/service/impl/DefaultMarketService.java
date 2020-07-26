package vstocks.db.service.impl;

import vstocks.db.service.MarketService;
import vstocks.db.store.MarketStore;
import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;

import javax.sql.DataSource;
import java.util.Optional;

public class DefaultMarketService extends BaseService implements MarketService {
    private final MarketStore marketStore;

    public DefaultMarketService(DataSource dataSource, MarketStore marketStore) {
        super(dataSource);
        this.marketStore = marketStore;
    }

    @Override
    public Optional<Market> get(String id) {
        return withConnection(conn -> marketStore.get(conn, id));
    }

    @Override
    public Results<Market> getAll(Page page) {
        return withConnection(conn -> marketStore.getAll(conn, page));
    }

    @Override
    public int add(Market market) {
        return withConnection(conn -> marketStore.add(conn, market));
    }

    @Override
    public int update(Market market) {
        return withConnection(conn -> marketStore.update(conn, market));
    }

    @Override
    public int delete(String id) {
        return withConnection(conn -> marketStore.delete(conn, id));
    }
}
