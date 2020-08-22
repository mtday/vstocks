package vstocks.db.jdbc;

import vstocks.db.BaseService;
import vstocks.model.*;
import vstocks.db.StockPriceDB;
import vstocks.db.jdbc.table.StockPriceTable;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class JdbcStockPriceDB extends BaseService implements StockPriceDB {
    private final StockPriceTable stockPriceTable = new StockPriceTable();

    public JdbcStockPriceDB(DataSource dataSource) {
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
    public int addAll(Collection<StockPrice> stockPrices) {
        return withConnection(conn -> stockPriceTable.addAll(conn, stockPrices));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> stockPriceTable.ageOff(conn, cutoff));
    }
}
