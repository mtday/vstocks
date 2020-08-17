package vstocks.db.jdbc.table;

import vstocks.model.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.SortDirection.DESC;

public class PricedStockJoin extends BaseTable {
    private final StockTable stockTable = new StockTable();
    private final StockPriceTable stockPriceTable = new StockPriceTable();

    private static final RowMapper<PricedStock> ROW_MAPPER = rs -> {
        Timestamp timestamp = rs.getTimestamp("timestamp");
        Instant instant = rs.wasNull() ? Instant.now() : timestamp.toInstant();
        long price = rs.getLong("price");
        price = rs.wasNull() ? 1 : price;
        return new PricedStock()
                .setMarket(Market.valueOf(rs.getString("market")))
                .setSymbol(rs.getString("symbol"))
                .setName(rs.getString("name"))
                .setProfileImage(rs.getString("profile_image"))
                .setTimestamp(instant)
                .setPrice(price);
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new LinkedHashSet<>(asList(MARKET.toSort(), SYMBOL.toSort(), TIMESTAMP.toSort(DESC)));
    }

    private void populateDeltas(Connection connection, List<PricedStock> pricedStocks) {
        if (pricedStocks.isEmpty()) {
            return;
        }

        Map<Market, Set<String>> marketSymbols = new HashMap<>();
        pricedStocks.forEach(s -> marketSymbols.computeIfAbsent(s.getMarket(), m -> new TreeSet<>()).add(s.getSymbol()));

        Map<Market, Map<String, List<StockPrice>>> prices = new HashMap<>();
        Consumer<StockPrice> consumer = stockPrice ->
                prices.computeIfAbsent(stockPrice.getMarket(), market -> new TreeMap<>())
                        .computeIfAbsent(stockPrice.getSymbol(), symbol -> new ArrayList<>())
                        .add(stockPrice);

        List<Object> params = new ArrayList<>();
        params.add(DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest());
        StringBuilder sql = new StringBuilder("SELECT * FROM stock_prices WHERE timestamp >= ?");
        marketSymbols.forEach((market, symbols) -> {
            sql.append(" AND (market = ? AND symbol = ANY(?)) ");
            params.add(market);
            params.add(symbols);
        });
        sql.append("ORDER BY market, symbol, timestamp DESC");
        stockPriceTable.consume(connection, StockPriceTable.ROW_MAPPER, consumer, sql.toString(), params.toArray());

        pricedStocks.forEach(pricedStock -> {
            List<StockPrice> stockPrices = Stream.of(prices.get(pricedStock.getMarket()))
                    .filter(Objects::nonNull)
                    .map(map -> map.get(pricedStock.getSymbol()))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseGet(Collections::emptyList);
            pricedStock.setDeltas(Delta.getDeltas(stockPrices, StockPrice::getTimestamp, StockPrice::getPrice));
        });
    }

    public Optional<PricedStock> get(Connection connection, Market market, String symbol) {
        String sql = "SELECT DISTINCT ON (s.market, s.symbol) s.*, p.timestamp, p.price FROM stocks s "
                + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                + "WHERE s.market = ? AND s.symbol = ? ORDER BY s.market, s.symbol, p.timestamp DESC";
        return getOne(connection, ROW_MAPPER, sql, market, symbol).map(pricedStock -> {
            populateDeltas(connection, singletonList(pricedStock));
            return pricedStock;
        });
    }

    public Results<PricedStock> getForMarket(Connection connection, Market market, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (s.market, s.symbol) s.*, p.timestamp, p.price FROM stocks s "
                + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                + "WHERE s.market = ? ORDER BY s.market, s.symbol, p.timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM ("
                + "SELECT DISTINCT ON (s.market, s.symbol) s.market, s.symbol FROM stocks s "
                + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                + "WHERE s.market = ? ORDER BY s.market, s.symbol, p.timestamp DESC"
                + ") AS data";
        Results<PricedStock> pricedStockResults = results(connection, ROW_MAPPER, page, sql, count, market);
        populateDeltas(connection, pricedStockResults.getResults());
        return pricedStockResults;
    }

    public Results<PricedStock> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (s.market, s.symbol) s.*, p.timestamp, p.price FROM stocks s "
                + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                + "ORDER BY s.market, s.symbol, p.timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM ("
                + "SELECT DISTINCT ON (s.market, s.symbol) s.market, s.symbol FROM stocks s "
                + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                + "ORDER BY s.market, s.symbol, p.timestamp DESC"
                + ") AS data";
        Results<PricedStock> pricedStockResults = results(connection, ROW_MAPPER, page, sql, count);
        populateDeltas(connection, pricedStockResults.getResults());
        return pricedStockResults;
    }

    public int add(Connection connection, PricedStock pricedStock) {
        int added = 0;
        added += stockTable.add(connection, pricedStock.asStock());
        added += stockPriceTable.add(connection, pricedStock.asStockPrice());
        return added > 0 ? 1 : 0;
    }
}
