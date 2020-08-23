package vstocks.db;

import vstocks.model.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class StockServiceImpl extends BaseService implements StockService {
    private final StockDB stockDB = new StockDB();

    public StockServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Stock> get(Market market, String symbol) {
        return withConnection(conn -> stockDB.get(conn, market, symbol));
    }

    @Override
    public Results<Stock> getForMarket(Market market, Page page, List<Sort> sort) {
        return withConnection(conn -> stockDB.getForMarket(conn, market, page, sort));
    }

    @Override
    public int consumeForMarket(Market market, Consumer<Stock> consumer, List<Sort> sort) {
        return withConnection(conn -> stockDB.consumeForMarket(conn, market, consumer, sort));
    }

    @Override
    public Results<Stock> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> stockDB.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<Stock> consumer, List<Sort> sort) {
        return withConnection(conn -> stockDB.consume(conn, consumer, sort));
    }

    @Override
    public int add(Stock stock) {
        return withConnection(conn -> stockDB.add(conn, stock));
    }

    @Override
    public int update(Stock stock) {
        return withConnection(conn -> stockDB.update(conn, stock));
    }

    @Override
    public int delete(Market market, String symbol) {
        return withConnection(conn -> stockDB.delete(conn, market, symbol));
    }

    @Override
    public int truncate() {
        return withConnection(stockDB::truncate);
    }
}
