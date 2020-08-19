package vstocks.db.jdbc;

import vstocks.db.UserCountDB;
import vstocks.db.jdbc.table.UserCountTable;
import vstocks.model.*;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Set;

public class JdbcUserCountDB extends BaseService implements UserCountDB {
    private final UserCountTable userCountTable = new UserCountTable();

    public JdbcUserCountDB(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public UserCount generate() {
        return withConnection(userCountTable::generate);
    }

    @Override
    public UserCount getLatest() {
        return withConnection(userCountTable::getLatest);
    }

    @Override
    public Results<UserCount> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> userCountTable.getAll(conn, page, sort));
    }

    @Override
    public int add(UserCount userCount) {
        return withConnection(conn -> userCountTable.add(conn, userCount));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> userCountTable.ageOff(conn, cutoff));
    }
}
