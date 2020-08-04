package vstocks.service.db.jdbc;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.PricedUserStock;
import vstocks.model.Results;
import vstocks.service.db.PricedUserStockService;
import vstocks.service.db.jdbc.table.PricedUserStockJoin;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.function.Consumer;

public class JdbcPricedUserStockService extends BaseService implements PricedUserStockService {
    private final PricedUserStockJoin pricedUserStockJoin = new PricedUserStockJoin();

    public JdbcPricedUserStockService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<PricedUserStock> get(String userId, Market market, String symbol) {
        return withConnection(conn -> pricedUserStockJoin.get(conn, userId, market, symbol));
    }

    @Override
    public Results<PricedUserStock> getForUser(String userId, Page page) {
        return withConnection(conn -> pricedUserStockJoin.getForUser(conn, userId, page));
    }

    @Override
    public Results<PricedUserStock> getForStock(Market market, String symbol, Page page) {
        return withConnection(conn -> pricedUserStockJoin.getForStock(conn, market, symbol, page));
    }

    @Override
    public Results<PricedUserStock> getAll(Page page) {
        return withConnection(conn -> pricedUserStockJoin.getAll(conn, page));
    }

    @Override
    public int consume(Consumer<PricedUserStock> consumer) {
        return withConnection(conn -> pricedUserStockJoin.consume(conn, consumer));
    }
}
