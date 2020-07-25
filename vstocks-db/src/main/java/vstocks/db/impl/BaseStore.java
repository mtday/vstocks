package vstocks.db.impl;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class BaseStore<T> {
    private final DataSource dataSource;

    BaseStore(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    private void populatePreparedStatement(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Collection) {
                // only collections containing strings are supported
                Object[] param = ((Collection<?>) params[i]).toArray();
                ps.setArray(i + 1, ps.getConnection().createArrayOf("varchar", param));
            } else {
                ps.setObject(i + 1, params[i]);
            }
        }
    }

    Optional<T> getOne(RowMapper<T> rowMapper, String sql, Object... params) {
        try (Connection conn = getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            populatePreparedStatement(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return of(rowMapper.map(rs));
                } else {
                    return empty();
                }
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException("Failed to fetch object from database", sqlException);
        }
    }

    List<T> getList(RowMapper<T> rowMapper, String sql, Object... params) {
        try (Connection conn = getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            populatePreparedStatement(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(rowMapper.map(rs));
                }
                return results;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException("Failed to fetch objects from database", sqlException);
        }
    }

    int update(RowSetter<T> rowSetter, String sql, Collection<T> objects) {
        if (objects.isEmpty()) {
            return 0;
        }
        try (Connection conn = getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int updated = 0;
            for (T object : objects) {
                rowSetter.set(ps, object);
                updated += ps.executeUpdate();
            }
            return updated;
        } catch (SQLException sqlException) {
            throw new RuntimeException("Failed to perform database update", sqlException);
        }
    }

    int update(String sql, Object... params) {
        try (Connection conn = getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            populatePreparedStatement(ps, params);
            return ps.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RuntimeException("Failed to perform database update", sqlException);
        }
    }
}
