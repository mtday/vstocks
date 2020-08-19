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
    public UserCount generateTotal() {
        return withConnection(userCountTable::generateTotal);
    }

    @Override
    public UserCount generateActive() {
        return withConnection(userCountTable::generateActive);
    }

    @Override
    public UserCount getLatestTotal() {
        return withConnection(userCountTable::getLatestTotal);
    }

    @Override
    public UserCount getLatestActive() {
        return withConnection(userCountTable::getLatestActive);
    }

    @Override
    public Results<UserCount> getAllTotal(Page page, Set<Sort> sort) {
        return withConnection(conn -> userCountTable.getAllTotal(conn, page, sort));
    }

    @Override
    public Results<UserCount> getAllActive(Page page, Set<Sort> sort) {
        return withConnection(conn -> userCountTable.getAllActive(conn, page, sort));
    }

    @Override
    public int addTotal(UserCount userCount) {
        return withConnection(conn -> userCountTable.addTotal(conn, userCount));
    }

    @Override
    public int addActive(UserCount userCount) {
        return withConnection(conn -> userCountTable.addActive(conn, userCount));
    }

    @Override
    public int ageOffTotal(Instant cutoff) {
        return withConnection(conn -> userCountTable.ageOffTotal(conn, cutoff));
    }

    @Override
    public int ageOffActive(Instant cutoff) {
        return withConnection(conn -> userCountTable.ageOffActive(conn, cutoff));
    }
}
