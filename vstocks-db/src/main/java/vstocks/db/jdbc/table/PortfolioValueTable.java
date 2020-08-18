package vstocks.db.jdbc.table;

import vstocks.model.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.SortDirection.DESC;

public class PortfolioValueTable extends BaseTable {
    private static final RowMapper<PortfolioValue> ROW_MAPPER = rs -> {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 0L));

        Stream.of(rs.getString("market_values"))
                .filter(Objects::nonNull)
                .map(values -> values.split(";"))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .filter(marketValue -> !marketValue.isEmpty())
                .map(marketValue -> marketValue.split(":", 2))
                .map(arr -> new SimpleEntry<>(Market.valueOf(arr[0]), Long.parseLong(arr[1])))
                .forEach(entry -> marketValues.put(entry.getKey(), entry.getValue()));

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

    public PortfolioValue generateForUser(Connection connection, String userId) {
        String sql = "SELECT uc.user_id, NOW() AS timestamp, uc.credits, COALESCE(market_values, '') AS market_values, "
                + "  credits + COALESCE(stock_total, 0) AS total "
                + "FROM user_credits uc LEFT JOIN ("
                + "  SELECT user_id, STRING_AGG(market || ':' || value, ';') AS market_values, SUM(value) AS stock_total FROM ("
                + "    SELECT user_id, market, SUM(value) AS value FROM ("
                + "      SELECT DISTINCT ON (us.user_id, us.market, us.symbol) "
                + "        us.user_id, us.market, us.symbol, us.shares * sp.price AS value "
                + "      FROM user_stocks us "
                + "      JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol) "
                + "      WHERE us.user_id = ? "
                + "      ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC"
                + "    ) AS priced_stocks GROUP BY user_id, market "
                + "  ) AS grouped_markets GROUP BY user_id "
                + ") AS stock_values ON (uc.user_id = stock_values.user_id) WHERE uc.user_id = ?";
        return getOne(connection, ROW_MAPPER, sql, userId, userId).orElseGet(() -> {
            Map<Market, Long> marketValues = new TreeMap<>();
            Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 0L));
            return new PortfolioValue()
                    .setUserId(userId)
                    .setTimestamp(Instant.now().truncatedTo(SECONDS))
                    .setCredits(0)
                    .setMarketValues(marketValues)
                    .setTotal(0);
        });
    }

    public int generateAll(Connection connection, Consumer<PortfolioValue> consumer) {
        String sql = "SELECT uc.user_id, NOW() AS timestamp, uc.credits, COALESCE(market_values, '') AS market_values, "
                + "  credits + COALESCE(stock_total, 0) AS total "
                + "FROM user_credits uc LEFT JOIN ("
                + "  SELECT user_id, STRING_AGG(market || ':' || value, ';') AS market_values, SUM(value) AS stock_total FROM ("
                + "    SELECT user_id, market, SUM(value) AS value FROM ("
                + "      SELECT DISTINCT ON (us.user_id, us.market, us.symbol) "
                + "        us.user_id, us.market, us.symbol, us.shares * sp.price AS value "
                + "      FROM user_stocks us "
                + "      JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol) "
                + "      ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC"
                + "    ) AS priced_stocks GROUP BY user_id, market "
                + "  ) AS grouped_markets GROUP BY user_id "
                + ") AS stock_values ON (uc.user_id = stock_values.user_id) ORDER BY total DESC";
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    private void populateDeltas(Connection connection, List<PortfolioValue> portfolioValues) {
        if (portfolioValues.isEmpty()) {
            return;
        }

        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();
        Set<String> userIds = portfolioValues.stream().map(PortfolioValue::getUserId).collect(toSet());

        Map<String, List<PortfolioValue>> values = new HashMap<>();
        Consumer<PortfolioValue> consumer = portfolioValue ->
                values.computeIfAbsent(portfolioValue.getUserId(), userId -> new ArrayList<>()).add(portfolioValue);

        String sql = "SELECT * FROM portfolio_values "
                + "WHERE timestamp >= ? AND user_id = ANY(?) "
                + "ORDER BY user_id, timestamp DESC";
        consume(connection, ROW_MAPPER, consumer, sql, earliest, userIds);

        portfolioValues.forEach(portfolioValue -> {
            List<PortfolioValue> pv = Stream.of(values.get(portfolioValue.getUserId()))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseGet(Collections::emptyList);
            portfolioValue.setDeltas(Delta.getDeltas(pv, PortfolioValue::getTimestamp, PortfolioValue::getTotal));
        });
    }

    public Optional<PortfolioValue> getLatest(Connection connection, String userId) {
        String sql = "SELECT * FROM portfolio_values WHERE user_id = ? ORDER BY timestamp DESC LIMIT 1";
        return getOne(connection, ROW_MAPPER, sql, userId).map(portfolioValue -> {
            populateDeltas(connection, singletonList(portfolioValue));
            return portfolioValue;
        });
    }

    public Results<PortfolioValue> getLatest(Connection connection, Collection<String> userIds, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (user_id) * FROM portfolio_values WHERE user_id = ANY(?) "
                + "ORDER BY user_id, timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM (SELECT DISTINCT ON (user_id) * FROM portfolio_values "
                + "WHERE user_id = ANY(?)) AS data";
        Results<PortfolioValue> portfolioValueResults = results(connection, ROW_MAPPER, page, sql, count, userIds);
        populateDeltas(connection, portfolioValueResults.getResults());
        return portfolioValueResults;
    }

    public Results<PortfolioValue> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM portfolio_values %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM portfolio_values";
        Results<PortfolioValue> portfolioValueResults = results(connection, ROW_MAPPER, page, sql, count);
        populateDeltas(connection, portfolioValueResults.getResults());
        return portfolioValueResults;
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
