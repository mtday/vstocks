package vstocks.service.db.jdbc;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.PricedStock;
import vstocks.model.Results;
import vstocks.service.db.PricedStockService;
import vstocks.service.db.jdbc.table.PricedStockJoin;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.function.Consumer;

public class JdbcPricedStockService extends BaseService implements PricedStockService {
    private final PricedStockJoin pricedStockJoin = new PricedStockJoin();

    public JdbcPricedStockService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<PricedStock> get(Market market, String symbol) {
        return withConnection(conn -> pricedStockJoin.get(conn, market, symbol));
    }

    @Override
    public Results<PricedStock> getForMarket(Market market, Page page) {
        return withConnection(conn -> pricedStockJoin.getForMarket(conn, market, page));
    }

    @Override
    public int consumeForMarket(Market market, Consumer<PricedStock> consumer) {
        return withConnection(conn -> pricedStockJoin.consumeForMarket(conn, market, consumer));
    }

    @Override
    public Results<PricedStock> getAll(Page page) {
        return withConnection(conn -> pricedStockJoin.getAll(conn, page));
    }

    @Override
    public int consume(Consumer<PricedStock> consumer) {
        return withConnection(conn -> pricedStockJoin.consume(conn, consumer));
    }
}
