package vstocks.service.jdbc;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Stock;
import vstocks.service.StockService;
import vstocks.service.jdbc.table.StockTable;

import javax.sql.DataSource;
import java.util.Optional;

public class JdbcStockService extends BaseService implements StockService {
    private final StockTable stockTable = new StockTable();

    public JdbcStockService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Stock> get(String id) {
        return withConnection(conn -> stockTable.get(conn, id));
    }

    @Override
    public Results<Stock> getForMarket(String marketId, Page page) {
        return withConnection(conn -> stockTable.getForMarket(conn, marketId, page));
    }

    @Override
    public Results<Stock> getAll(Page page) {
        return withConnection(conn -> stockTable.getAll(conn, page));
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
    public int delete(String id) {
        return withConnection(conn -> stockTable.delete(conn, id));
    }
}
