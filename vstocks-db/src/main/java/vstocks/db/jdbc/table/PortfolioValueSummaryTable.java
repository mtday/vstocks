package vstocks.db.jdbc.table;

import vstocks.model.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.joining;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.SortDirection.DESC;

public class PortfolioValueSummaryTable extends BaseTable {
    private static final RowMapper<PortfolioValueSummary> ROW_MAPPER = rs -> {
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

        return new PortfolioValueSummary()
                .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                .setCredits(rs.getLong("credits"))
                .setMarketValues(marketValues)
                .setTotal(rs.getLong("total"));
    };

    private static final RowSetter<PortfolioValueSummary> INSERT_ROW_SETTER = (ps, portfolioValueSummary) -> {
        String marketValues = Stream.of(portfolioValueSummary.getMarketValues())
                .filter(Objects::nonNull)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .map(entry -> entry.getKey().name() + ":" + entry.getValue())
                .collect(joining(";"));

        int index = 0;
        ps.setTimestamp(++index, Timestamp.from(portfolioValueSummary.getTimestamp()));
        ps.setLong(++index, portfolioValueSummary.getCredits());
        ps.setString(++index, marketValues);
        ps.setLong(++index, portfolioValueSummary.getTotal());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return singleton(TIMESTAMP.toSort(DESC));
    }

    public PortfolioValueSummary generate(Connection connection) {
        String sql = "SELECT NOW() AS timestamp, SUM(credits) AS credits,"
                + "       STRING_AGG(market_values, ';') AS market_values, SUM(credits) + SUM(stock_total) AS total FROM ("
                + "  SELECT 0 AS credits, STRING_AGG(market || ':' || value, ';') AS market_values,"
                + "         SUM(value) AS stock_total FROM ("
                + "    SELECT market, SUM(value) AS value FROM ("
                + "      SELECT DISTINCT ON (us.user_id, us.market, us.symbol)"
                + "        us.user_id, us.market, us.symbol, us.shares * sp.price AS value"
                + "      FROM user_stocks us"
                + "      JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol)"
                + "      ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC"
                + "    ) AS market_data GROUP BY market"
                + "  ) AS summary_data"
                + "  UNION"
                + "  SELECT SUM(credits) AS credits, '' AS market_values, 0 AS stock_total"
                + "  FROM user_credits"
                + ") AS unioned_data";
        return getOne(connection, ROW_MAPPER, sql).orElse(null); // there will always be a result
    }

    public PortfolioValueSummary getLatest(Connection connection) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        List<PortfolioValueSummary> values = new ArrayList<>();
        String sql = "SELECT * FROM portfolio_value_summaries WHERE timestamp >= ? ORDER BY timestamp DESC";
        consume(connection, ROW_MAPPER, values::add, sql, earliest);

        PortfolioValueSummary portfolioValueSummary = values.stream().findFirst().orElseGet(() -> {
            Map<Market, Long> marketValues = new TreeMap<>();
            Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 0L));
            return new PortfolioValueSummary()
                    .setTimestamp(Instant.now().truncatedTo(SECONDS))
                    .setCredits(0)
                    .setMarketValues(marketValues)
                    .setTotal(0);
        });
        portfolioValueSummary.setDeltas(Delta.getDeltas(values, PortfolioValueSummary::getTimestamp, PortfolioValueSummary::getTotal));
        return portfolioValueSummary;
    }

    public Results<PortfolioValueSummary> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM portfolio_value_summaries %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM portfolio_value_summaries";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, PortfolioValueSummary portfolioValueSummary) {
        String sql = "INSERT INTO portfolio_value_summaries (timestamp, credits, market_values, total) "
                + "VALUES (?, ?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT portfolio_value_summaries_pk DO UPDATE "
                + "SET credits = EXCLUDED.credits, market_values = EXCLUDED.market_values, total = EXCLUDED.total "
                + "WHERE portfolio_value_summaries.credits != EXCLUDED.credits OR "
                + "portfolio_value_summaries.market_values != EXCLUDED.market_values OR "
                + "portfolio_value_summaries.total != EXCLUDED.total";
        return update(connection, INSERT_ROW_SETTER, sql, portfolioValueSummary);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM portfolio_value_summaries WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM portfolio_value_summaries");
    }
}
