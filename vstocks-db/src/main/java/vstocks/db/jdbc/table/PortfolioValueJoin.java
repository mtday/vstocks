package vstocks.db.jdbc.table;

import vstocks.model.Market;
import vstocks.model.PortfolioValue;
import vstocks.model.Sort;

import java.sql.Connection;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static vstocks.model.DatabaseField.TOTAL;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.Sort.SortDirection.DESC;

public class PortfolioValueJoin extends BaseTable {
    private static final RowMapper<PortfolioValue> ROW_MAPPER = rs -> {
        Map<Market, Long> marketValues = Stream.of(rs.getString("market_values"))
                .filter(Objects::nonNull)
                .map(values -> values.split(";"))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .filter(marketValue -> !marketValue.isEmpty())
                .map(marketValue -> marketValue.split(":", 2))
                .map(arr -> new AbstractMap.SimpleEntry<>(Market.valueOf(arr[0]), Long.parseLong(arr[1])))
                .collect(toMap(Entry::getKey, Entry::getValue));

        return new PortfolioValue()
                .setUserId(rs.getString("user_id"))
                .setCredits(rs.getLong("credits"))
                .setMarketValues(marketValues)
                .setTotal(rs.getLong("total"));
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new HashSet<>(asList(TOTAL.toSort(DESC), USER_ID.toSort()));
    }

    public Optional<PortfolioValue> get(Connection connection, String userId) {
        String sql = "SELECT uc.user_id, uc.credits, COALESCE(market_values, '') AS market_values, "
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
        return getOne(connection, ROW_MAPPER, sql, userId, userId);
    }

    public int consume(Connection connection, Consumer<PortfolioValue> consumer) {
        String sql = "SELECT uc.user_id, uc.credits, COALESCE(market_values, '') AS market_values, "
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
}
