package vstocks.service.db.jdbc;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.StockPrice;
import vstocks.service.db.StockPriceService;
import vstocks.service.db.jdbc.table.StockPriceTable;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public class JdbcStockPriceService extends BaseService implements StockPriceService {
    private final StockPriceTable stockPriceTable = new StockPriceTable();

    public JdbcStockPriceService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<StockPrice> get(String id) {
        return withConnection(conn -> stockPriceTable.get(conn, id));
    }

    @Override
    public Results<StockPrice> getLatest(Collection<String> stockIds, Page page) {
        return withConnection(conn -> stockPriceTable.getLatest(conn, stockIds, page));
    }

    @Override
    public Results<StockPrice> getForStock(String stockId, Page page) {
        return withConnection(conn -> stockPriceTable.getForStock(conn, stockId, page));
    }

    @Override
    public Results<StockPrice> getAll(Page page) {
        return withConnection(conn -> stockPriceTable.getAll(conn, page));
    }

    @Override
    public int consume(Consumer<StockPrice> consumer) {
        return withConnection(conn -> stockPriceTable.consume(conn, consumer));
    }

    @Override
    public int add(StockPrice stockPrice) {
        return withConnection(conn -> stockPriceTable.add(conn, stockPrice));
    }

    @Override
    public int delete(String id) {
        return withConnection(conn -> stockPriceTable.delete(conn, id));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> stockPriceTable.ageOff(conn, cutoff));
    }
}
