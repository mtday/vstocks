package vstocks.db.jdbc.table;

import vstocks.model.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Sort.SortDirection.DESC;

public class StockPriceTable extends BaseTable {
    private static final RowMapper<StockPrice> ROW_MAPPER = rs ->
            new StockPrice()
                    .setMarket(Market.valueOf(rs.getString("market")))
                    .setSymbol(rs.getString("symbol"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant())
                    .setPrice(rs.getInt("price"));

    private static final RowSetter<StockPrice> INSERT_ROW_SETTER = (ps, stockPrice) -> {
        int index = 0;
        ps.setString(++index, stockPrice.getMarket().name());
        ps.setString(++index, stockPrice.getSymbol());
        ps.setTimestamp(++index, Timestamp.from(stockPrice.getTimestamp()));
        ps.setInt(++index, stockPrice.getPrice());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new HashSet<>(asList(MARKET.toSort(), SYMBOL.toSort(), TIMESTAMP.toSort(DESC)));
    }

    public Optional<StockPrice> getLatest(Connection connection, Market market, String symbol) {
        String query = format("SELECT * FROM stock_prices WHERE market = ? AND symbol = ? %s LIMIT 1", getSort(getDefaultSort()));
        return getOne(connection, ROW_MAPPER, query, market, symbol);
    }

    public Results<StockPrice> getLatest(Connection connection, Market market, Collection<String> symbols, Page page, Set<Sort> sort) {
        String query = "SELECT DISTINCT ON (symbol) * FROM stock_prices WHERE market = ? AND symbol = ANY(?) "
                + "ORDER BY symbol, timestamp DESC LIMIT ? OFFSET ?";
        if (sort != null && !sort.isEmpty()) {
            query = format("SELECT * FROM (%s) AS data %s", query, getSort(sort));
        }
        String countQuery = "SELECT COUNT(*) FROM (SELECT DISTINCT ON (symbol) * FROM stock_prices "
                + "WHERE market = ? AND symbol = ANY(?)) AS data";
        return results(connection, ROW_MAPPER, page, query, countQuery, market, symbols);
    }

    public Results<StockPrice> getForStock(Connection connection, Market market, String symbol, Page page, Set<Sort> sort) {
        String query = format("SELECT * FROM stock_prices WHERE market = ? AND symbol = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String countQuery = "SELECT COUNT(*) FROM stock_prices WHERE market = ? AND symbol = ?";
        return results(connection, ROW_MAPPER, page, query, countQuery, market, symbol);
    }

    public Results<StockPrice> getAll(Connection connection, Page page, Set<Sort> sort) {
        String query = format("SELECT * FROM stock_prices %s LIMIT ? OFFSET ?", getSort(sort));
        String countQuery = "SELECT COUNT(*) FROM stock_prices";
        return results(connection, ROW_MAPPER, page, query, countQuery);
    }

    public int consume(Connection connection, Consumer<StockPrice> consumer, Set<Sort> sort) {
        String sql = format("SELECT * FROM stock_prices %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int add(Connection connection, StockPrice stockPrice) {
        String sql = "INSERT INTO stock_prices (market, symbol, timestamp, price) VALUES (?, ?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT stock_prices_pk DO UPDATE "
                + "SET price = EXCLUDED.price WHERE stock_prices.price != EXCLUDED.price";
        return update(connection, INSERT_ROW_SETTER, sql, stockPrice);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM stock_prices WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM stock_prices");
    }
}
