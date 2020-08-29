package vstocks.db.portfolio;

import vstocks.db.BaseDB;
import vstocks.db.RowMapper;
import vstocks.model.Market;
import vstocks.model.Sort;
import vstocks.model.portfolio.PortfolioValue;

import java.sql.Connection;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

class PortfolioValueDB extends BaseDB {
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
                .setCredits(rs.getLong("credits"))
                .setMarketTotal(rs.getLong("market_total"))
                .setMarketValues(marketValues)
                .setTotal(rs.getLong("total"));
    };

    @Override
    protected List<Sort> getDefaultSort() {
        return emptyList();
    }

    public Optional<PortfolioValue> getForUser(Connection connection, String userId) {
        String sql = "SELECT uc.user_id, uc.credits, COALESCE(market_values, '') AS market_values, "
                + "  COALESCE(market_total, 0) AS market_total, credits + COALESCE(market_total, 0) AS total "
                + "FROM user_credits uc LEFT JOIN ("
                + "  SELECT user_id, STRING_AGG(market || ':' || value, ';') AS market_values, "
                + "         SUM(value) AS market_total FROM ("
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
}
