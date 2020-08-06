package vstocks.db.jdbc;

import vstocks.model.*;
import vstocks.db.PricedUserStockDB;
import vstocks.db.jdbc.table.PricedUserStockJoin;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class JdbcPricedUserStockDB extends BaseService implements PricedUserStockDB {
    private final PricedUserStockJoin pricedUserStockJoin = new PricedUserStockJoin();

    public JdbcPricedUserStockDB(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<PricedUserStock> get(String userId, Market market, String symbol) {
        return withConnection(conn -> pricedUserStockJoin.get(conn, userId, market, symbol));
    }

    @Override
    public Results<PricedUserStock> getForUser(String userId, Page page, Set<Sort> sort) {
        return withConnection(conn -> pricedUserStockJoin.getForUser(conn, userId, page, sort));
    }

    @Override
    public Results<PricedUserStock> getForStock(Market market, String symbol, Page page, Set<Sort> sort) {
        return withConnection(conn -> pricedUserStockJoin.getForStock(conn, market, symbol, page, sort));
    }

    @Override
    public Results<PricedUserStock> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> pricedUserStockJoin.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<PricedUserStock> consumer, Set<Sort> sort) {
        return withConnection(conn -> pricedUserStockJoin.consume(conn, consumer, sort));
    }
}
