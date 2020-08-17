package vstocks.db.jdbc;

import vstocks.db.PricedStockDB;
import vstocks.db.jdbc.table.PricedStockJoin;
import vstocks.model.*;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Set;

public class JdbcPricedStockDB extends BaseService implements PricedStockDB {
    private final PricedStockJoin pricedStockJoin = new PricedStockJoin();

    public JdbcPricedStockDB(DataSource dataSource) {
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
    public Results<PricedStock> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> pricedStockJoin.getAll(conn, page, sort));
    }

    @Override
    public int add(PricedStock pricedStock) {
        return withConnection(conn -> pricedStockJoin.add(conn, pricedStock));
    }
}
