package vstocks.service.jdbc;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.service.MarketService;
import vstocks.service.jdbc.table.MarketTable;

import javax.sql.DataSource;
import java.util.Optional;

public class JdbcMarketService extends BaseService implements MarketService {
    private final MarketTable marketTable = new MarketTable();

    public JdbcMarketService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Market> get(String id) {
        return withConnection(conn -> marketTable.get(conn, id));
    }

    @Override
    public Results<Market> getAll(Page page) {
        return withConnection(conn -> marketTable.getAll(conn, page));
    }

    @Override
    public int add(Market market) {
        return withConnection(conn -> marketTable.add(conn, market));
    }

    @Override
    public int update(Market market) {
        return withConnection(conn -> marketTable.update(conn, market));
    }

    @Override
    public int delete(String id) {
        return withConnection(conn -> marketTable.delete(conn, id));
    }
}
