package vstocks.service.jdbc.table;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Stock;

import java.sql.Connection;
import java.util.Optional;

public class StockTable extends BaseTable {
    private static final RowMapper<Stock> ROW_MAPPER = rs ->
            new Stock()
                    .setMarketId(rs.getString("market_id"))
                    .setId(rs.getString("id"))
                    .setSymbol(rs.getString("symbol"))
                    .setName(rs.getString("name"));

    private static final RowSetter<Stock> INSERT_ROW_SETTER = (ps, stock) -> {
        int index = 0;
        ps.setString(++index, stock.getId());
        ps.setString(++index, stock.getMarketId());
        ps.setString(++index, stock.getSymbol());
        ps.setString(++index, stock.getName());
    };

    private static final RowSetter<Stock> UPDATE_ROW_SETTER = (ps, stock) -> {
        int index = 0;
        ps.setString(++index, stock.getSymbol());
        ps.setString(++index, stock.getName());
        ps.setString(++index, stock.getId());
        ps.setString(++index, stock.getSymbol());
        ps.setString(++index, stock.getName());
    };

    public Optional<Stock> get(Connection connection, String marketId, String stockId) {
        return getOne(connection, ROW_MAPPER, "SELECT * FROM stocks WHERE market_id = ? AND id = ?", marketId, stockId);
    }

    public Results<Stock> getForMarket(Connection connection, String marketId, Page page) {
        String query = "SELECT * FROM stocks WHERE market_id = ? ORDER BY symbol LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM stocks WHERE market_id = ?";
        return results(connection, ROW_MAPPER, page, query, countQuery, marketId);
    }

    public Results<Stock> getAll(Connection connection, Page page) {
        String query = "SELECT * FROM stocks ORDER BY market_id, symbol LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM stocks";
        return results(connection, ROW_MAPPER, page, query, countQuery);
    }

    public int add(Connection connection, Stock stock) {
        return update(connection, INSERT_ROW_SETTER, "INSERT INTO stocks (id, market_id, symbol, name) VALUES (?, ?, ?, ?)", stock);
    }

    public int update(Connection connection, Stock stock) {
        String sql = "UPDATE stocks SET symbol = ?, name = ? WHERE id = ? AND (symbol != ? OR name != ?)";
        return update(connection, UPDATE_ROW_SETTER, sql, stock);
    }

    public int delete(Connection connection, String marketId, String stockId) {
        return update(connection, "DELETE FROM stocks WHERE market_id = ? AND id = ?", marketId, stockId);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM stocks");
    }
}
