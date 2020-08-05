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
    public Optional<PricedStock> get(Market market, String symbol, Boolean active) {
        return withConnection(conn -> pricedStockJoin.get(conn, market, symbol, active));
    }

    @Override
    public Results<PricedStock> getForMarket(Market market, Boolean active, Page page, Set<Sort> sort) {
        return withConnection(conn -> pricedStockJoin.getForMarket(conn, market, active, page, sort));
    }

    @Override
    public int consumeForMarket(Market market, Boolean active, Consumer<PricedStock> consumer, Set<Sort> sort) {
        return withConnection(conn -> pricedStockJoin.consumeForMarket(conn, market, active, consumer, sort));
    }

    @Override
    public Results<PricedStock> getAll(Boolean active, Page page, Set<Sort> sort) {
        return withConnection(conn -> pricedStockJoin.getAll(conn, active, page, sort));
    }

    @Override
    public int consume(Boolean active, Consumer<PricedStock> consumer, Set<Sort> sort) {
        return withConnection(conn -> pricedStockJoin.consume(conn, active, consumer, sort));
    }

    @Override
    public int add(PricedStock pricedStock) {
        return withConnection(conn -> pricedStockJoin.add(conn, pricedStock));
    }
}
