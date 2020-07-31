package vstocks.service.db.jdbc.table;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.StockPrice;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

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

    public Optional<StockPrice> getLatest(Connection connection, Market market, String symbol) {
        String query = "SELECT * FROM stock_prices WHERE market = ? AND symbol = ? ORDER BY timestamp DESC LIMIT 1";
        return getOne(connection, ROW_MAPPER, query, market, symbol);
    }

    public Results<StockPrice> getLatest(Connection connection, Market market, Collection<String> symbols, Page page) {
        String query = "SELECT DISTINCT ON (symbol) * FROM stock_prices WHERE market = ? AND symbol = ANY(?) "
                + "ORDER BY symbol, timestamp DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM (SELECT DISTINCT ON (symbol) * FROM stock_prices "
                + "WHERE market = ? AND symbol = ANY(?)) AS data";
        return results(connection, ROW_MAPPER, page, query, countQuery, market, symbols);
    }

    public Results<StockPrice> getForStock(Connection connection, Market market, String symbol, Page page) {
        String query = "SELECT * FROM stock_prices WHERE market = ? AND symbol = ? "
                + "ORDER BY timestamp DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM stock_prices WHERE market = ? AND symbol = ?";
        return results(connection, ROW_MAPPER, page, query, countQuery, market, symbol);
    }

    public Results<StockPrice> getAll(Connection connection, Page page) {
        String query = "SELECT * FROM stock_prices ORDER BY market, symbol, timestamp DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM stock_prices";
        return results(connection, ROW_MAPPER, page, query, countQuery);
    }

    public int consume(Connection connection, Consumer<StockPrice> consumer) {
        String sql = "SELECT * FROM stock_prices ORDER BY market, symbol, timestamp DESC";
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int add(Connection connection, StockPrice stockPrice) {
        String sql = "INSERT INTO stock_prices (market, symbol, timestamp, price) VALUES (?, ?, ?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, stockPrice);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM stock_prices WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM stock_prices");
    }
}
