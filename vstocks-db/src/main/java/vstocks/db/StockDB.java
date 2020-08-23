package vstocks.db;

import vstocks.model.*;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.MARKET;
import static vstocks.model.DatabaseField.SYMBOL;

class StockDB extends BaseDB {
    private static final RowMapper<Stock> ROW_MAPPER = rs ->
            new Stock()
                    .setMarket(Market.valueOf(rs.getString("market")))
                    .setSymbol(rs.getString("symbol"))
                    .setName(rs.getString("name"))
                    .setProfileImage(rs.getString("profile_image"));

    private static final RowSetter<Stock> INSERT_ROW_SETTER = (ps, stock) -> {
        int index = 0;
        ps.setString(++index, stock.getMarket().name());
        ps.setString(++index, stock.getSymbol());
        ps.setString(++index, stock.getName());
        ps.setString(++index, stock.getProfileImage());
    };

    private static final RowSetter<Stock> UPDATE_ROW_SETTER = (ps, stock) -> {
        int index = 0;
        ps.setString(++index, stock.getName());
        ps.setString(++index, stock.getProfileImage());
        ps.setString(++index, stock.getMarket().name());
        ps.setString(++index, stock.getSymbol());
        ps.setString(++index, stock.getName());
        ps.setString(++index, stock.getProfileImage());
    };

    @Override
    protected List<Sort> getDefaultSort() {
        return asList(MARKET.toSort(), SYMBOL.toSort());
    }

    public Optional<Stock> get(Connection connection, Market market, String symbol) {
        String sql = "SELECT * FROM stocks WHERE market = ? AND symbol = ?";
        return getOne(connection, ROW_MAPPER, sql, market, symbol);
    }

    public Results<Stock> getForMarket(Connection connection, Market market, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM stocks WHERE market = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM stocks WHERE market = ?";
        return results(connection, ROW_MAPPER, page, sql, count, market);
    }

    public int consumeForMarket(Connection connection, Market market, Consumer<Stock> consumer, List<Sort> sort) {
        String sql = format("SELECT * FROM stocks WHERE market = ? %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql, market);
    }

    public Results<Stock> getAll(Connection connection, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM stocks %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM stocks";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int consume(Connection connection, Consumer<Stock> consumer, List<Sort> sort) {
        String sql = format("SELECT * FROM stocks %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int add(Connection connection, Stock stock) {
        String sql = "INSERT INTO stocks (market, symbol, name, profile_image) VALUES (?, ?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT stocks_pk "
                + "DO UPDATE set name = EXCLUDED.name, profile_image = EXCLUDED.profile_image "
                + "WHERE stocks.name != EXCLUDED.name "
                + "OR COALESCE(stocks.profile_image, '') != COALESCE(EXCLUDED.profile_image, '')";
        return update(connection, INSERT_ROW_SETTER, sql, stock);
    }

    public int update(Connection connection, Stock stock) {
        String sql = "UPDATE stocks SET name = ?, profile_image = ? "
                + "WHERE market = ? AND symbol = ? AND (name != ? OR COALESCE(profile_image, '') != COALESCE(?, ''))";
        return update(connection, UPDATE_ROW_SETTER, sql, stock);
    }

    public int delete(Connection connection, Market market, String symbol) {
        return update(connection, "DELETE FROM stocks WHERE market = ? AND symbol = ?", market, symbol);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM stocks");
    }
}
