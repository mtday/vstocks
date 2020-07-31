package vstocks.service.db.jdbc.table;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Stock;

import java.sql.Connection;
import java.util.Optional;
import java.util.function.Consumer;

public class StockTable extends BaseTable {
    private static final RowMapper<Stock> ROW_MAPPER = rs ->
            new Stock()
                    .setMarket(Market.valueOf(rs.getString("market")))
                    .setSymbol(rs.getString("symbol"))
                    .setName(rs.getString("name"));

    private static final RowSetter<Stock> INSERT_ROW_SETTER = (ps, stock) -> {
        int index = 0;
        ps.setString(++index, stock.getMarket().name());
        ps.setString(++index, stock.getSymbol());
        ps.setString(++index, stock.getName());
    };

    private static final RowSetter<Stock> UPDATE_ROW_SETTER = (ps, stock) -> {
        int index = 0;
        ps.setString(++index, stock.getName());
        ps.setString(++index, stock.getMarket().name());
        ps.setString(++index, stock.getSymbol());
        ps.setString(++index, stock.getName());
    };

    public Optional<Stock> get(Connection connection, Market market, String symbol) {
        return getOne(connection, ROW_MAPPER, "SELECT * FROM stocks WHERE market = ? AND symbol = ?", market, symbol);
    }

    public Results<Stock> getForMarket(Connection connection, Market market, Page page) {
        String query = "SELECT * FROM stocks WHERE market = ? ORDER BY symbol LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM stocks WHERE market = ?";
        return results(connection, ROW_MAPPER, page, query, countQuery, market);
    }

    public Results<Stock> getAll(Connection connection, Page page) {
        String query = "SELECT * FROM stocks ORDER BY market, symbol LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM stocks";
        return results(connection, ROW_MAPPER, page, query, countQuery);
    }

    public int consume(Connection connection, Consumer<Stock> consumer) {
        String sql = "SELECT * FROM stocks ORDER BY market, symbol";
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int add(Connection connection, Stock stock) {
        return update(connection, INSERT_ROW_SETTER, "INSERT INTO stocks (market, symbol, name) VALUES (?, ?, ?)", stock);
    }

    public int update(Connection connection, Stock stock) {
        String sql = "UPDATE stocks SET name = ? WHERE market = ? AND symbol = ? AND name != ?";
        return update(connection, UPDATE_ROW_SETTER, sql, stock);
    }

    public int delete(Connection connection, Market market, String symbol) {
        return update(connection, "DELETE FROM stocks WHERE market = ? AND symbol = ?", market, symbol);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM stocks");
    }
}
