package vstocks.db;

import vstocks.model.*;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

public class StockPriceChangeServiceImpl extends BaseService implements StockPriceChangeService {
    private final StockPriceChangeDB stockPriceChangeDB = new StockPriceChangeDB();

    public StockPriceChangeServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long setCurrentBatch(long batch) {
        return withConnection(conn -> stockPriceChangeDB.setCurrentBatch(conn, batch));
    }

    @Override
    public int generate() {
        return withConnection(stockPriceChangeDB::generate);
    }

    @Override
    public StockPriceChangeCollection getLatest(Market market, String symbol) {
        return withConnection(conn -> stockPriceChangeDB.getLatest(conn, market, symbol));
    }

    @Override
    public Results<StockPriceChange> getForMarket(Market market, Page page, List<Sort> sort) {
        return withConnection(conn -> stockPriceChangeDB.getForMarket(conn, market, page, sort));
    }

    @Override
    public Results<StockPriceChange> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> stockPriceChangeDB.getAll(conn, page, sort));
    }

    @Override
    public int add(StockPriceChange stockPriceChange) {
        return withConnection(conn -> stockPriceChangeDB.add(conn, stockPriceChange));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> stockPriceChangeDB.ageOff(conn, cutoff));
    }

    @Override
    public int truncate() {
        return withConnection(stockPriceChangeDB::truncate);
    }
}
