package vstocks.db;

import vstocks.model.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.DAYS;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.SortDirection.DESC;

class StockPriceChangeDB extends BaseDB {
    private static final String BATCH_SEQUENCE = "stock_price_changes_batch_sequence";

    private static final RowMapper<StockPriceChange> ROW_MAPPER = rs ->
            new StockPriceChange()
                    .setBatch(rs.getLong("batch"))
                    .setMarket(Market.valueOf(rs.getString("market")))
                    .setSymbol(rs.getString("symbol"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setPrice(rs.getLong("price"))
                    .setChange(rs.getLong("change"))
                    .setPercent(rs.getFloat("percent"));

    private static final RowSetter<StockPriceChange> INSERT_ROW_SETTER = (ps, stockPriceChange) -> {
        int index = 0;
        ps.setLong(++index, stockPriceChange.getBatch());
        ps.setString(++index, stockPriceChange.getMarket().name());
        ps.setString(++index, stockPriceChange.getSymbol());
        ps.setTimestamp(++index, Timestamp.from(stockPriceChange.getTimestamp()));
        ps.setLong(++index, stockPriceChange.getPrice());
        ps.setLong(++index, stockPriceChange.getChange());
        ps.setFloat(++index, stockPriceChange.getPercent());
    };

    @Override
    protected List<Sort> getDefaultSort() {
        return asList(BATCH.toSort(DESC), PERCENT.toSort(DESC), CHANGE.toSort(DESC));
    }

    public long setCurrentBatch(Connection connection, long batch) {
        return setSequenceValue(connection, BATCH_SEQUENCE, batch);
    }

    public int generate(Connection connection) {
        long batch = getNextSequenceValue(connection, BATCH_SEQUENCE);
        Instant oneDayAgo = Instant.now().minusSeconds(DAYS.toSeconds(1)).truncatedTo(SECONDS);
        String sql = "INSERT INTO stock_price_changes (batch, market, symbol, timestamp, price, change, percent)"
                + "(SELECT ? AS batch, market, symbol, NOW() AS timestamp, last AS price,"
                + "       (last - first) AS change, (CAST((last - first) AS FLOAT) / first * 100) AS percent FROM ("
                + "  WITH latest_prices AS ("
                + "    SELECT DISTINCT ON (market, symbol) market, symbol, price"
                + "    FROM stock_prices"
                + "    WHERE timestamp >= ?"
                + "    ORDER BY market, symbol, timestamp DESC"
                + "  ), earliest_prices AS ("
                + "    SELECT DISTINCT ON (market, symbol) market, symbol, price"
                + "    FROM stock_prices"
                + "    WHERE timestamp >= ?"
                + "    ORDER BY market, symbol, timestamp"
                + "  )"
                + "  SELECT lp.market, lp.symbol, lp.price AS last, ep.price AS first"
                + "  FROM latest_prices lp"
                + "  JOIN earliest_prices ep ON (lp.market = ep.market AND lp.symbol = ep.symbol)"
                + "  ORDER BY lp.market, lp.symbol"
                + ") AS price_data)";
        return update(connection, sql, batch, oneDayAgo, oneDayAgo);
    }

    public StockPriceChangeCollection getLatest(Connection connection, Market market, String symbol) {
        Instant earliest = DeltaInterval.getLast().getEarliest();

        String sql = "SELECT * FROM stock_price_changes "
                + "WHERE timestamp >= ? AND market = ? AND symbol = ? ORDER BY timestamp DESC";
        List<StockPriceChange> changes = new ArrayList<>();
        consume(connection, ROW_MAPPER, changes::add, sql, earliest, market, symbol);

        return new StockPriceChangeCollection().setChanges(changes);
    }

    public Results<StockPriceChange> getForMarket(Connection connection, Market market, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM stock_price_changes WHERE market = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM stock_price_changes WHERE market = ?";
        return results(connection, ROW_MAPPER, page, sql, count, market);
    }

    public Results<StockPriceChange> getAll(Connection connection, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM stock_price_changes %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM stock_price_changes";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, StockPriceChange stockPriceChange) {
        String sql = "INSERT INTO stock_price_changes (batch, market, symbol, timestamp, price, change, percent) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT stock_price_changes_pk "
                + "DO UPDATE SET price = EXCLUDED.price, change = EXCLUDED.change, percent = EXCLUDED.percent "
                + "WHERE stock_price_changes.price != EXCLUDED.price OR stock_price_changes.change != EXCLUDED.change "
                + "OR stock_price_changes.percent != EXCLUDED.percent";
        return update(connection, INSERT_ROW_SETTER, sql, stockPriceChange);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM stock_price_changes WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM stock_price_changes");
    }
}
