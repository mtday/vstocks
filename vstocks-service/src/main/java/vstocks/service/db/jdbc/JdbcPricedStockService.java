package vstocks.service.db.jdbc;

import vstocks.model.*;
import vstocks.service.db.PricedStockService;
import vstocks.service.db.jdbc.table.PricedStockJoin;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Set;
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
    public Results<PricedStock> getForMarket(Market market, Page page, Set<Sort> sort) {
        return withConnection(conn -> pricedStockJoin.getForMarket(conn, market, page, sort));
    }

    @Override
    public int consumeForMarket(Market market, Consumer<PricedStock> consumer, Set<Sort> sort) {
        return withConnection(conn -> pricedStockJoin.consumeForMarket(conn, market, consumer, sort));
    }

    @Override
    public Results<PricedStock> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> pricedStockJoin.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<PricedStock> consumer, Set<Sort> sort) {
        return withConnection(conn -> pricedStockJoin.consume(conn, consumer, sort));
    }

    @Override
    public int add(PricedStock pricedStock) {
        return withConnection(conn -> pricedStockJoin.add(conn, pricedStock));
    }
}
