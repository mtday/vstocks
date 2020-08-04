package vstocks.service.db.jdbc;

import vstocks.model.*;
import vstocks.service.db.StockPriceService;
import vstocks.service.db.jdbc.table.StockPriceTable;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class JdbcStockPriceService extends BaseService implements StockPriceService {
    private final StockPriceTable stockPriceTable = new StockPriceTable();

    public JdbcStockPriceService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<StockPrice> getLatest(Market market, String symbol) {
        return withConnection(conn -> stockPriceTable.getLatest(conn, market, symbol));
    }

    @Override
    public Results<StockPrice> getLatest(Market market, Collection<String> symbols, Page page, Set<Sort> sort) {
        return withConnection(conn -> stockPriceTable.getLatest(conn, market, symbols, page, sort));
    }

    @Override
    public Results<StockPrice> getForStock(Market market, String symbol, Page page, Set<Sort> sort) {
        return withConnection(conn -> stockPriceTable.getForStock(conn, market, symbol, page, sort));
    }

    @Override
    public Results<StockPrice> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> stockPriceTable.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<StockPrice> consumer, Set<Sort> sort) {
        return withConnection(conn -> stockPriceTable.consume(conn, consumer, sort));
    }

    @Override
    public int add(StockPrice stockPrice) {
        return withConnection(conn -> stockPriceTable.add(conn, stockPrice));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> stockPriceTable.ageOff(conn, cutoff));
    }
}
