package vstocks.db;

import vstocks.model.*;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class StockPriceServiceImpl extends BaseService implements StockPriceService {
    private final StockPriceDB stockPriceDB = new StockPriceDB();

    public StockPriceServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<StockPrice> getLatest(Market market, String symbol) {
        return withConnection(conn -> stockPriceDB.getLatest(conn, market, symbol));
    }

    @Override
    public Results<StockPrice> getLatest(Market market, Collection<String> symbols, Page page, List<Sort> sort) {
        return withConnection(conn -> stockPriceDB.getLatest(conn, market, symbols, page, sort));
    }

    @Override
    public Results<StockPrice> getForStock(Market market, String symbol, Page page, List<Sort> sort) {
        return withConnection(conn -> stockPriceDB.getForStock(conn, market, symbol, page, sort));
    }

    @Override
    public Results<StockPrice> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> stockPriceDB.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<StockPrice> consumer, List<Sort> sort) {
        return withConnection(conn -> stockPriceDB.consume(conn, consumer, sort));
    }

    @Override
    public int add(StockPrice stockPrice) {
        return withConnection(conn -> stockPriceDB.add(conn, stockPrice));
    }

    @Override
    public int addAll(Collection<StockPrice> stockPrices) {
        return withConnection(conn -> stockPriceDB.addAll(conn, stockPrices));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> stockPriceDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(stockPriceDB::truncate);
    }
}
