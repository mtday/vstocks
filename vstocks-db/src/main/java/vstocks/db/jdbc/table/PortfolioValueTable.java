package vstocks.db.jdbc.table;

import vstocks.model.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.SortDirection.DESC;

public class PortfolioValueTable extends BaseTable {
    private static final RowMapper<PortfolioValue> ROW_MAPPER = rs -> {
        Map<Market, Long> marketValues = Stream.of(rs.getString("market_values"))
                .filter(Objects::nonNull)
                .map(values -> values.split(";"))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .filter(marketValue -> !marketValue.isEmpty())
                .map(marketValue -> marketValue.split(":", 2))
                .map(arr -> new AbstractMap.SimpleEntry<>(Market.valueOf(arr[0]), Long.parseLong(arr[1])))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new PortfolioValue()
                .setUserId(rs.getString("user_id"))
                .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                .setCredits(rs.getLong("credits"))
                .setMarketValues(marketValues)
                .setTotal(rs.getLong("total"));
    };

    private static final RowSetter<PortfolioValue> INSERT_ROW_SETTER = (ps, portfolioValue) -> {
        String marketValues = Stream.of(portfolioValue.getMarketValues())
                .filter(Objects::nonNull)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .map(entry -> entry.getKey().name() + ":" + entry.getValue())
                .collect(joining(";"));

        int index = 0;
        ps.setString(++index, portfolioValue.getUserId());
        ps.setTimestamp(++index, Timestamp.from(portfolioValue.getTimestamp()));
        ps.setLong(++index, portfolioValue.getCredits());
        ps.setString(++index, marketValues);
        ps.setLong(++index, portfolioValue.getTotal());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new LinkedHashSet<>(asList(USER_ID.toSort(), TIMESTAMP.toSort(DESC)));
    }

    public Optional<PortfolioValue> getLatest(Connection connection, String userId) {
        String sql = "SELECT * FROM portfolio_values WHERE user_id = ? ORDER BY timestamp DESC LIMIT 1";
        return getOne(connection, ROW_MAPPER, sql, userId);
    }

    public Results<PortfolioValue> getLatest(Connection connection, Collection<String> userIds, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (user_id) * FROM portfolio_values WHERE user_id = ANY(?) "
                + "ORDER BY user_id, timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM (SELECT DISTINCT ON (user_id) * FROM portfolio_values "
                + "WHERE user_id = ANY(?)) AS data";
        return results(connection, ROW_MAPPER, page, sql, count, userIds);
    }

    public Results<PortfolioValue> getForUser(Connection connection, String userId, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM portfolio_values WHERE user_id = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM portfolio_values WHERE user_id = ?";
        return results(connection, ROW_MAPPER, page, sql, count, userId);
    }

    public List<PortfolioValue> getForUserSince(Connection connection, String userId, Instant earliest, Set<Sort> sort) {
        String sql = format("SELECT * FROM portfolio_values WHERE user_id = ? AND timestamp >= ? %s", getSort(sort));
        return getList(connection, ROW_MAPPER, sql, userId, earliest);
    }

    public Results<PortfolioValue> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM portfolio_values %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM portfolio_values";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int consume(Connection connection, Consumer<PortfolioValue> consumer, Set<Sort> sort) {
        String sql = format("SELECT * FROM portfolio_values %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int add(Connection connection, PortfolioValue portfolioValue) {
        return addAll(connection, singleton(portfolioValue));
    }

    public int addAll(Connection connection, Collection<PortfolioValue> portfolioValues) {
        String sql = "INSERT INTO portfolio_values (user_id, timestamp, credits, market_values, total) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT portfolio_values_pk DO UPDATE "
                + "SET credits = EXCLUDED.credits, market_values = EXCLUDED.market_values, total = EXCLUDED.total "
                + "WHERE portfolio_values.credits != EXCLUDED.credits OR "
                + "portfolio_values.market_values != EXCLUDED.market_values OR "
                + "portfolio_values.total != EXCLUDED.total";
        return updateBatch(connection, INSERT_ROW_SETTER, sql, portfolioValues);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM portfolio_values WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM portfolio_values");
    }
}
