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
                    .setId(rs.getString("id"))
                    .setMarket(Market.valueOf(rs.getString("market")))
                    .setStockId(rs.getString("stock_id"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant())
                    .setPrice(rs.getInt("price"));

    private static final RowSetter<StockPrice> INSERT_ROW_SETTER = (ps, stockPrice) -> {
        int index = 0;
        ps.setString(++index, stockPrice.getId());
        ps.setString(++index, stockPrice.getMarket().name());
        ps.setString(++index, stockPrice.getStockId());
        ps.setTimestamp(++index, Timestamp.from(stockPrice.getTimestamp()));
        ps.setInt(++index, stockPrice.getPrice());
    };

    public Optional<StockPrice> get(Connection connection, String id) {
        return getOne(connection, ROW_MAPPER, "SELECT * FROM stock_prices WHERE id = ?", id);
    }

    public Optional<StockPrice> getLatest(Connection connection, String stockId) {
        String query = "SELECT * FROM stock_prices WHERE stock_id = ? ORDER BY timestamp DESC LIMIT 1";
        return getOne(connection, ROW_MAPPER, query, stockId);
    }

    public Results<StockPrice> getLatest(Connection connection, Collection<String> stockIds, Page page) {
        String query = "SELECT DISTINCT ON (stock_id) * FROM stock_prices WHERE stock_id = ANY(?) "
                + "ORDER BY stock_id, timestamp DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM (SELECT DISTINCT ON (stock_id) * FROM stock_prices WHERE stock_id = ANY(?)) AS data";
        return results(connection, ROW_MAPPER, page, query, countQuery, stockIds);
    }

    public Results<StockPrice> getForStock(Connection connection, String stockId, Page page) {
        String query = "SELECT * FROM stock_prices WHERE stock_id = ? ORDER BY timestamp DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM stock_prices WHERE stock_id = ?";
        return results(connection, ROW_MAPPER, page, query, countQuery, stockId);
    }

    public Results<StockPrice> getAll(Connection connection, Page page) {
        String query = "SELECT * FROM stock_prices ORDER BY market, stock_id, timestamp DESC LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM stock_prices";
        return results(connection, ROW_MAPPER, page, query, countQuery);
    }

    public int consume(Connection connection, Consumer<StockPrice> consumer) {
        String sql = "SELECT * FROM stock_prices ORDER BY market, stock_id, timestamp DESC";
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int add(Connection connection, StockPrice stockPrice) {
        String sql = "INSERT INTO stock_prices (id, market, stock_id, timestamp, price) VALUES (?, ?, ?, ?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, stockPrice);
    }

    public int delete(Connection connection, String id) {
        return update(connection, "DELETE FROM stock_prices WHERE id = ?", id);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM stock_prices WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM stock_prices");
    }
}
