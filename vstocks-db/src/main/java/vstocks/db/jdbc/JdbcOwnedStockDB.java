package vstocks.db.jdbc;

import vstocks.db.BaseService;
import vstocks.db.OwnedStockDB;
import vstocks.db.jdbc.table.OwnedStockJoin;
import vstocks.model.Market;
import vstocks.model.Sort;
import vstocks.model.Stock;

import javax.sql.DataSource;
import java.util.Set;
import java.util.function.Consumer;

public class JdbcOwnedStockDB extends BaseService implements OwnedStockDB {
    private final OwnedStockJoin ownedStockJoin = new OwnedStockJoin();

    public JdbcOwnedStockDB(DataSource dataSource) {
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
