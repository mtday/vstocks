package vstocks.db.jdbc.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementCreator {
    PreparedStatement create(Connection connection) throws SQLException;
}
