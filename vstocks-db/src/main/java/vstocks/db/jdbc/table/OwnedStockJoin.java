package vstocks.db.jdbc.table;

import vstocks.model.Market;
import vstocks.model.Sort;
import vstocks.model.Stock;

import java.sql.Connection;
import java.util.HashSet;
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
        return new HashSet<>(asList(MARKET.toSort(), SYMBOL.toSort()));
    }

    public int consumeForMarket(Connection connection, Market market, Consumer<Stock> consumer, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "  WITH owned_stocks AS ("
                + "    SELECT DISTINCT market, symbol FROM user_stocks WHERE market = ? AND shares > 0"
                + "  ) SELECT s.* FROM stocks s "
                + "  JOIN owned_stocks os ON (s.market = os.market AND s.symbol = os.symbol)"
                + ") AS data %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql, market);
    }

    public int consume(Connection connection, Consumer<Stock> consumer, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "  WITH owned_stocks AS ("
                + "    SELECT DISTINCT market, symbol FROM user_stocks WHERE shares > 0"
                + "  ) SELECT s.* FROM stocks s "
                + "  JOIN owned_stocks os ON (s.market = os.market AND s.symbol = os.symbol)"
                + ") AS data %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql);
    }
}
