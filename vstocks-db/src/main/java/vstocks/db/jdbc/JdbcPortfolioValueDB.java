package vstocks.db.jdbc;

import vstocks.db.PortfolioValueDB;
import vstocks.db.jdbc.table.PortfolioValueJoin;
import vstocks.model.PortfolioValue;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.function.Consumer;

public class JdbcPortfolioValueDB extends BaseService implements PortfolioValueDB {
    private final PortfolioValueJoin accountValueTable = new PortfolioValueJoin();

    public JdbcPortfolioValueDB(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<PortfolioValue> get(String userId) {
        return withConnection(conn -> accountValueTable.get(conn, userId));
    }

    @Override
    public int consume(Consumer<PortfolioValue> consumer) {
        return withConnection(conn -> accountValueTable.consume(conn, consumer));
    }
}
