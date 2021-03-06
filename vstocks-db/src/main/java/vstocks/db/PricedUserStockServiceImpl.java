package vstocks.db;

import vstocks.model.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class PricedUserStockServiceImpl extends BaseService implements PricedUserStockService {
    private final PricedUserStockDB pricedUserStockJoin = new PricedUserStockDB();

    public PricedUserStockServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<PricedUserStock> get(String userId, Market market, String symbol) {
        return withConnection(conn -> pricedUserStockJoin.get(conn, userId, market, symbol));
    }

    @Override
    public Results<PricedUserStock> getForUser(String userId, Page page, List<Sort> sort) {
        return withConnection(conn -> pricedUserStockJoin.getForUser(conn, userId, page, sort));
    }

    @Override
    public Results<PricedUserStock> getForUserMarket(String userId, Market market, Page page, List<Sort> sort) {
        return withConnection(conn -> pricedUserStockJoin.getForUserMarket(conn, userId, market, page, sort));
    }

    @Override
    public Results<PricedUserStock> getForStock(Market market, String symbol, Page page, List<Sort> sort) {
        return withConnection(conn -> pricedUserStockJoin.getForStock(conn, market, symbol, page, sort));
    }

    @Override
    public Results<PricedUserStock> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> pricedUserStockJoin.getAll(conn, page, sort));
    }
}
