package vstocks.db;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.joining;

public class BaseDB {
    private int populatePreparedStatement(PreparedStatement ps, Object... params) throws SQLException {
        int index = 0;
        for (Object param : params) {
            if (param instanceof Collection) {
                // Converts collection items into strings
                Object[] array = ((Collection<?>) param).stream().map(String::valueOf).toArray();
                ps.setArray(++index, ps.getConnection().createArrayOf("varchar", array));
            } else if (param instanceof Enum) {
                ps.setString(++index, ((Enum<?>) param).name());
            } else if (param instanceof Instant) {
                ps.setTimestamp(++index, Timestamp.from((Instant) param));
            } else if (param != null) {
                ps.setObject(++index, param);
            }
        }
        return index;
    }

    protected List<Sort> getDefaultSort() {
        return emptyList();
    }

    protected String getSort(List<Sort> sort) {
        if (sort == null || sort.isEmpty()) {
            return "ORDER BY " + getDefaultSort().stream().map(Sort::toString).collect(joining(", "));
        }
        return "ORDER BY " + sort.stream().map(Sort::toString).collect(joining(", "));
    }

    private long sequence(Connection connection, String sql, Object... params) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            populatePreparedStatement(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException("Failed to fetch manipulate sequence in database", sqlException);
        }
    }

    protected long getCurrentSequenceValue(Connection connection, String sequenceName) {
        return sequence(connection, "SELECT currval(?)", sequenceName);
    }

    protected long getNextSequenceValue(Connection connection, String sequenceName) {
        return sequence(connection, "SELECT nextval(?)", sequenceName);
    }

    protected long setSequenceValue(Connection connection, String sequenceName, long value) {
        return sequence(connection, "SELECT setval(?, ?)", sequenceName, value);
    }

    protected int getCount(Connection connection, String sql, Object... params) {
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

    protected <T> Optional<T> getOne(Connection connection, RowMapper<T> rowMapper, String sql, Object... params) {
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

    protected <T> List<T> getList(Connection connection, RowMapper<T> rowMapper, String sql, Object... params) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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

    protected <T> Results<T> results(Connection connection,
                                     RowMapper<T> rowMapper,
                                     Page page,
                                     String query,
                                     String countQuery,
                                     Object... params) {
        Results<T> results = new Results<T>().setPage(page);
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            int index = populatePreparedStatement(ps, params);
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

    protected <T> int consume(Connection connection, RowMapper<T> rowMapper, Consumer<T> consumer, String sql, Object... params) {
        PreparedStatementCreator psc = conn -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            populatePreparedStatement(ps, params);
            return ps;
        };
        return consume(connection, psc, rowMapper, consumer);
    }

    protected <T> int consume(Connection connection, PreparedStatementCreator psc, RowMapper<T> rowMapper, Consumer<T> consumer) {
        try (PreparedStatement ps = psc.create(connection)) {
            try (ResultSet rs = ps.executeQuery()) {
                int consumed = 0;
                while (rs.next()) {
                    consumer.accept(rowMapper.map(rs));
                    consumed++;
                }
                return consumed;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException("Failed to consume objects from database", sqlException);
        }
    }

    protected <T> int update(Connection connection, RowSetter<T> rowSetter, String sql, T object) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            rowSetter.set(ps, object);
            return ps.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RuntimeException("Failed to perform database update", sqlException);
        }
    }

    protected <T> int updateBatch(Connection connection, RowSetter<T> rowSetter, String sql, Collection<T> objects) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int updated = 0;
            for (T object : objects) {
                rowSetter.set(ps, object);
                updated += ps.executeUpdate();
            }
            return updated;
        } catch (SQLException sqlException) {
            throw new RuntimeException("Failed to perform database updates", sqlException);
        }
    }

    protected int update(Connection connection, String sql, Object... params) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            populatePreparedStatement(ps, params);
            return ps.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RuntimeException("Failed to perform database update", sqlException);
        }
    }
}
