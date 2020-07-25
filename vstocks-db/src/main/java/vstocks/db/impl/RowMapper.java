package vstocks.db.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {
    T map(ResultSet resultSet) throws SQLException;
}
