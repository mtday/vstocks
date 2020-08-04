package vstocks.service.db.jdbc.table;

import vstocks.model.*;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.*;

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

    @Override
    protected Set<Sort> getDefaultSort() {
        return new HashSet<>(asList(MARKET.toSort(), SYMBOL.toSort()));
    }

    public Optional<Stock> get(Connection connection, Market market, String symbol) {
        return getOne(connection, ROW_MAPPER, "SELECT * FROM stocks WHERE market = ? AND symbol = ?", market, symbol);
    }

    public Results<Stock> getForMarket(Connection connection, Market market, Page page, Set<Sort> sort) {
        String query = format("SELECT * FROM stocks WHERE market = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String countQuery = "SELECT COUNT(*) FROM stocks WHERE market = ?";
        return results(connection, ROW_MAPPER, page, query, countQuery, market);
    }

    public int consumeForMarket(Connection connection, Market market, Consumer<Stock> consumer, Set<Sort> sort) {
        String sql = format("SELECT * FROM stocks WHERE market = ? %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql, market);
    }

    public Results<Stock> getAll(Connection connection, Page page, Set<Sort> sort) {
        String query = format("SELECT * FROM stocks %s LIMIT ? OFFSET ?", getSort(sort));
        String countQuery = "SELECT COUNT(*) FROM stocks";
        return results(connection, ROW_MAPPER, page, query, countQuery);
    }

    public int consume(Connection connection, Consumer<Stock> consumer, Set<Sort> sort) {
        String sql = format("SELECT * FROM stocks %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int add(Connection connection, Stock stock) {
        String sql = "INSERT INTO stocks (market, symbol, name) VALUES (?, ?, ?) ON CONFLICT ON CONSTRAINT stocks_pk "
                + "DO UPDATE set name = EXCLUDED.name WHERE stocks.name != EXCLUDED.name";
        return update(connection, INSERT_ROW_SETTER, sql, stock);
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
