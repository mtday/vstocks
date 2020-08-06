package vstocks.db.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class BaseService {
    private final DataSource dataSource;

    protected BaseService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Similar to java.util.Function, but allows an exception to be thrown.
    @FunctionalInterface
    public interface ExceptionFunction<T, R> {
        R apply(T t) throws Exception;
    }

    protected <T> T withConnection(ExceptionFunction<Connection, T> fn) {
        try (Connection conn = dataSource.getConnection()) {
            try {
                return fn.apply(conn);
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException(e);
            } finally {
                conn.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
