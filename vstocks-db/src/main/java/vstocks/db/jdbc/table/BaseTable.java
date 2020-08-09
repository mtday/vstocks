package vstocks.db.jdbc.table;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import vstocks.model.DatabaseField;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.joining;

public abstract class BaseTable {
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
            } else if (param instanceof Range) {
                // Only ranges containing numbers and Instants are supported
                Range<?> range = (Range<?>) param;
                if (range.hasLowerBound()) {
                    if (range.lowerEndpoint() instanceof Instant) {
                        ps.setTimestamp(++index, Timestamp.from((Instant) range.lowerEndpoint()));
                    } else {
                        ps.setObject(++index, range.lowerEndpoint());
                    }
                }
                if (range.hasUpperBound()) {
                    if (range.upperEndpoint() instanceof Instant) {
                        ps.setTimestamp(++index, Timestamp.from((Instant) range.upperEndpoint()));
                    } else {
                        ps.setObject(++index, range.upperEndpoint());
                    }
                }
            } else if (param != null) {
                ps.setObject(++index, param);
            }
        }
        return index;
    }

    protected abstract Set<Sort> getDefaultSort();

    protected String getSort(Set<Sort> sort) {
        if (sort == null || sort.isEmpty()) {
            return "ORDER BY " + getDefaultSort().stream().map(Sort::toString).collect(joining(", "));
        }
        return "ORDER BY " + sort.stream().map(Sort::toString).collect(joining(", "));
    }

    protected Optional<String> getSearchFilter(DatabaseField field, Collection<?> collection) {
        if (collection != null && !collection.isEmpty()) {
            return Optional.of(String.format("%s = ANY(?)", field.getField()));
        }
        return Optional.empty();
    }

    protected Optional<String> getSearchFilter(DatabaseField field, Range<?> range) {
        List<String> clauses = new ArrayList<>(2);
        if (range != null) {
            if (range.hasLowerBound()) {
                if (range.lowerBoundType() == BoundType.CLOSED) {
                    clauses.add(String.format("%s >= ?", field.getField()));
                } else {
                    clauses.add(String.format("%s > ?", field.getField()));
                }
            }
            if (range.hasUpperBound()) {
                if (range.upperBoundType() == BoundType.CLOSED) {
                    clauses.add(String.format("%s <= ?", field.getField()));
                } else {
                    clauses.add(String.format("%s < ?", field.getField()));
                }
            }
        }
        if (!clauses.isEmpty()) {
            return Optional.of(String.join(" AND ", clauses));
        }
        return Optional.empty();
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

    protected int update(Connection connection, String sql, Object... params) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            populatePreparedStatement(ps, params);
            return ps.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RuntimeException("Failed to perform database update", sqlException);
        }
    }
}
