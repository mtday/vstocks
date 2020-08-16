package vstocks.db.jdbc.table;

import vstocks.model.Market;
import vstocks.model.Sort;
import vstocks.model.Stock;

import java.sql.Connection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.MARKET;
import static vstocks.model.DatabaseField.SYMBOL;

public class OwnedStockJoin extends BaseTable {
    private static final RowMapper<Stock> ROW_MAPPER = rs ->
            new Stock()
                    .setMarket(Market.valueOf(rs.getString("market")))
                    .setSymbol(rs.getString("symbol"))
                    .setName(rs.getString("name"))
                    .setProfileImage(rs.getString("profile_image"));

    @Override
    protected Set<Sort> getDefaultSort() {
        return new LinkedHashSet<>(asList(MARKET.toSort(), SYMBOL.toSort()));
    }

    public int consumeForMarket(Connection connection, Market market, Consumer<Stock> consumer, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "  SELECT DISTINCT ON (symbol) s.* FROM stocks s "
                + "  JOIN user_stocks us ON (us.market = ? AND s.symbol = us.symbol AND us.shares > 0) "
                + "  ORDER BY symbol, user_id "
                + ") AS data %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql, market);
    }

    public int consume(Connection connection, Consumer<Stock> consumer, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "  SELECT DISTINCT ON (market, symbol) s.* FROM stocks s "
                + "  JOIN user_stocks us ON (s.market = us.market AND s.symbol = us.symbol AND us.shares > 0) "
                + "  ORDER BY market, symbol, user_id "
                + ") AS data %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql);
    }
}
