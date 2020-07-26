package vstocks.db.store.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface RowSetter<T> {
    void set(PreparedStatement ps, T value) throws SQLException;
}
