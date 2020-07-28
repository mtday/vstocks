package vstocks.service.jdbc.table;

import vstocks.model.Page;
import vstocks.model.Results;

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

public class BaseTable {
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

    int getCount(Connection connection, String sql, Object... params) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            populatePreparedStatement(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException("Failed to fetch count from database", sqlException);
        }
    }

    <T> Optional<T> getOne(Connection connection, RowMapper<T> rowMapper, String sql, Object... params) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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

    <T> List<T> getList(Connection connection, RowMapper<T> rowMapper, String sql, Object... params) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            populatePreparedStatement(ps, params);
            List<T> values = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    values.add(rowMapper.map(rs));
                }
                return values;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException("Failed to fetch objects from database", sqlException);
        }
    }

    <T> Results<T> results(Connection connection,
                       RowMapper<T> rowMapper,
                       Page page,
                       String query,
                       String countQuery,
                       Object... params) {
        Results<T> results = new Results<T>().setPage(page);
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            populatePreparedStatement(ps, params);
            int index = params.length;
            ps.setInt(++index, page.getSize());
            ps.setInt(++index, (page.getPage() - 1) * page.getSize());
            try (ResultSet rs = ps.executeQuery()) {
                results.setResults(new ArrayList<>(page.getSize()));
                while (rs.next()) {
                    results.getResults().add(rowMapper.map(rs));
                }
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException("Failed to fetch objects from database", sqlException);
        }
        if (page.getPage() != 1 || results.getResults().size() == page.getSize()) {
            try (PreparedStatement ps = connection.prepareStatement(countQuery)) {
                populatePreparedStatement(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        results.setTotal(rs.getInt(1));
                    }
                }
            } catch (SQLException sqlException) {
                throw new RuntimeException("Failed to fetch object count from database", sqlException);
            }
        } else {
            results.setTotal(results.getResults().size());
        }
        return results;
    }

    <T> int update(Connection connection, RowSetter<T> rowSetter, String sql, T object) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            rowSetter.set(ps, object);
            return ps.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RuntimeException("Failed to perform database update", sqlException);
        }
    }

    int update(Connection connection, String sql, Object... params) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            populatePreparedStatement(ps, params);
            return ps.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RuntimeException("Failed to perform database update", sqlException);
        }
    }
}
