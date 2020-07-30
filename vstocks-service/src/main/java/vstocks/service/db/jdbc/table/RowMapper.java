package vstocks.service.db.jdbc.table;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {
    T map(ResultSet resultSet) throws SQLException;
}
