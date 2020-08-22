package vstocks.db;

import vstocks.model.Market;
import vstocks.model.Sort;
import vstocks.model.Stock;

import javax.sql.DataSource;
import java.util.Set;
import java.util.function.Consumer;

public class OwnedStockServiceImpl extends BaseService implements OwnedStockService {
    private final OwnedStockDB ownedStockJoin = new OwnedStockDB();

    public OwnedStockServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int consumeForMarket(Market market, Consumer<Stock> consumer, Set<Sort> sort) {
        return withConnection(conn -> ownedStockJoin.consumeForMarket(conn, market, consumer, sort));
    }

    @Override
    public int consume(Consumer<Stock> consumer, Set<Sort> sort) {
        return withConnection(conn -> ownedStockJoin.consume(conn, consumer, sort));
    }
}
