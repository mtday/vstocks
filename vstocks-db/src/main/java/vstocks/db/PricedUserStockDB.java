package vstocks.db;

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

class PricedUserStockDB extends BaseTable {
    private final StockPriceDB stockPriceTable = new StockPriceDB();

    private static final RowMapper<PricedUserStock> ROW_MAPPER = rs -> {
        Timestamp timestamp = rs.getTimestamp("timestamp");
        Instant instant = rs.wasNull() ? Instant.now() : timestamp.toInstant();
        long price = rs.getLong("price");
        price = rs.wasNull() ? 1 : price;
        return new PricedUserStock()
                .setUserId(rs.getString("user_id"))
                .setMarket(Market.valueOf(rs.getString("market")))
                .setSymbol(rs.getString("symbol"))
                .setShares(rs.getInt("shares"))
                .setTimestamp(instant)
                .setPrice(price);
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new LinkedHashSet<>(asList(MARKET.toSort(), SYMBOL.toSort(), TIMESTAMP.toSort(DESC)));
    }

    private void populateDeltas(Connection connection, List<PricedUserStock> pricedUserStocks) {
        if (pricedUserStocks.isEmpty()) {
            return;
        }

        Map<Market, Set<String>> marketSymbols = new HashMap<>();
        pricedUserStocks.forEach(s -> marketSymbols.computeIfAbsent(s.getMarket(), m -> new TreeSet<>()).add(s.getSymbol()));

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
        stockPriceTable.consume(connection, StockPriceDB.ROW_MAPPER, consumer, sql.toString(), params.toArray());

        pricedUserStocks.forEach(pricedUserStock -> {
            List<StockPrice> stockPrices = Stream.of(prices.get(pricedUserStock.getMarket()))
                    .filter(Objects::nonNull)
                    .map(map -> map.get(pricedUserStock.getSymbol()))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseGet(Collections::emptyList);
            pricedUserStock.setDeltas(Delta.getDeltas(stockPrices, StockPrice::getTimestamp, StockPrice::getPrice));
        });
    }

    public Optional<PricedUserStock> get(Connection connection, String userId, Market market, String symbol) {
        String sql = "SELECT DISTINCT ON (u.market, u.symbol) u.*, p.timestamp, p.price FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "WHERE u.user_id = ? AND u.market = ? AND u.symbol = ? ORDER BY u.market, u.symbol, p.timestamp DESC";
        return getOne(connection, ROW_MAPPER, sql, userId, market, symbol).map(pricedUserStock -> {
            populateDeltas(connection, singletonList(pricedUserStock));
            return pricedUserStock;
        });
    }

    public Results<PricedUserStock> getForUser(Connection connection, String userId, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (u.market, u.symbol) u.*, p.timestamp, p.price FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "WHERE u.user_id = ? ORDER BY u.market, u.symbol, p.timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM ("
                + "SELECT DISTINCT ON (u.market, u.symbol) u.market, u.symbol FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "WHERE u.user_id = ? ORDER BY u.market, u.symbol, p.timestamp DESC"
                + ") AS data";
        Results<PricedUserStock> pricedUserStockResults = results(connection, ROW_MAPPER, page, sql, count, userId);
        populateDeltas(connection, pricedUserStockResults.getResults());
        return pricedUserStockResults;
    }

    public Results<PricedUserStock> getForStock(Connection connection, Market market, String symbol, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (u.user_id, u.market, u.symbol) u.*, p.timestamp, p.price FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "WHERE u.market = ? AND u.symbol = ? "
                + "ORDER BY u.user_id, u.market, u.symbol, p.timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM ("
                + "SELECT DISTINCT ON (u.user_id, u.market, u.symbol) u.user_id FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "WHERE u.market = ? AND u.symbol = ? ORDER BY u.user_id, u.market, u.symbol, p.timestamp DESC"
                + ") AS data";
        Results<PricedUserStock> pricedUserStockResults = results(connection, ROW_MAPPER, page, sql, count, market, symbol);
        populateDeltas(connection, pricedUserStockResults.getResults());
        return pricedUserStockResults;
    }

    public Results<PricedUserStock> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM ("
                + "SELECT DISTINCT ON (u.user_id, u.market, u.symbol) u.*, p.timestamp, p.price FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "ORDER BY u.user_id, u.market, u.symbol, p.timestamp DESC LIMIT ? OFFSET ?"
                + ") AS data %s", getSort(sort));
        String count = "SELECT COUNT(*) FROM ("
                + "SELECT DISTINCT ON (u.user_id, u.market, u.symbol) u.user_id, u.market, u.symbol FROM user_stocks u "
                + "LEFT JOIN stock_prices p ON (u.market = p.market AND u.symbol = p.symbol) "
                + "ORDER BY u.user_id, u.market, u.symbol, p.timestamp DESC"
                + ") AS data";
        Results<PricedUserStock> pricedUserStockResults = results(connection, ROW_MAPPER, page, sql, count);
        populateDeltas(connection, pricedUserStockResults.getResults());
        return pricedUserStockResults;
    }
}
