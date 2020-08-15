package vstocks.db.jdbc;

import vstocks.model.*;
import vstocks.db.StockDB;
import vstocks.db.jdbc.table.StockTable;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class JdbcStockDB extends BaseService implements StockDB {
    private final StockTable stockTable = new StockTable();

    public JdbcStockDB(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Stock> get(Market market, String symbol) {
        return withConnection(conn -> stockTable.get(conn, market, symbol));
    }

    @Override
    public Results<Stock> getForMarket(Market market, Page page, Set<Sort> sort) {
        return withConnection(conn -> stockTable.getForMarket(conn, market, page, sort));
    }

    @Override
    public int consumeForMarket(Market market, Consumer<Stock> consumer, Set<Sort> sort) {
        return withConnection(conn -> stockTable.consumeForMarket(conn, market, consumer, sort));
    }

    @Override
    public Results<Stock> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> stockTable.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<Stock> consumer, Set<Sort> sort) {
        return withConnection(conn -> stockTable.consume(conn, consumer, sort));
    }

    @Override
    public int add(Stock stock) {
        return withConnection(conn -> stockTable.add(conn, stock));
    }

    @Override
    public int update(Stock stock) {
        return withConnection(conn -> stockTable.update(conn, stock));
    }

    @Override
    public int delete(Market market, String symbol) {
        return withConnection(conn -> stockTable.delete(conn, market, symbol));
    }
}
