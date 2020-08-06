package vstocks.db.jdbc.table;

import vstocks.model.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Sort.SortDirection.DESC;

public class PricedStockJoin extends BaseTable {
    private final StockTable stockTable = new StockTable();
    private final StockPriceTable stockPriceTable = new StockPriceTable();

    private static final RowMapper<PricedStock> ROW_MAPPER = rs -> {
        Timestamp timestamp = rs.getTimestamp("timestamp");
        Instant instant = rs.wasNull() ? Instant.now() : timestamp.toInstant();
        int price = rs.getInt("price");
        price = rs.wasNull() ? 1 : price;
        return new PricedStock()
                .setMarket(Market.valueOf(rs.getString("market")))
                .setSymbol(rs.getString("symbol"))
                .setName(rs.getString("name"))
                .setActive(rs.getBoolean("active"))
                .setTimestamp(instant)
                .setPrice(price);
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new HashSet<>(asList(MARKET.toSort(), SYMBOL.toSort(), TIMESTAMP.toSort(DESC)));
    }

    public Optional<PricedStock> get(Connection connection, Market market, String symbol, Boolean active) {
        if (active != null) {
            String sql = "SELECT DISTINCT ON (s.market, s.symbol) s.*, p.timestamp, p.price FROM stocks s "
                    + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                    + "WHERE s.market = ? AND s.symbol = ? AND s.active = ? "
                    + "ORDER BY s.market, s.symbol, p.timestamp DESC";
            return getOne(connection, ROW_MAPPER, sql, market, symbol, active);
        } else {
            String sql = "SELECT DISTINCT ON (s.market, s.symbol) s.*, p.timestamp, p.price FROM stocks s "
                    + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                    + "WHERE s.market = ? AND s.symbol = ? ORDER BY s.market, s.symbol, p.timestamp DESC";
            return getOne(connection, ROW_MAPPER, sql, market, symbol);
        }
    }

    public Results<PricedStock> getForMarket(Connection connection, Market market, Boolean active, Page page, Set<Sort> sort) {
        if (active != null) {
            String query = "SELECT DISTINCT ON (s.market, s.symbol) s.*, p.timestamp, p.price FROM stocks s "
                    + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                    + "WHERE s.market = ? AND s.active = ? "
                    + "ORDER BY s.market, s.symbol, p.timestamp DESC LIMIT ? OFFSET ?";
            if (sort != null && !sort.isEmpty()) {
                query = format("SELECT * FROM (%s) AS data %s", query, getSort(sort));
            }
            String countQuery = "SELECT COUNT(*) FROM ("
                    + "SELECT DISTINCT ON (s.market, s.symbol) s.market, s.symbol FROM stocks s "
                    + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                    + "WHERE s.market = ? AND s.active = ? ORDER BY s.market, s.symbol, p.timestamp DESC"
                    + ") AS data";
            return results(connection, ROW_MAPPER, page, query, countQuery, market, active);
        } else {
            String query = "SELECT DISTINCT ON (s.market, s.symbol) s.*, p.timestamp, p.price FROM stocks s "
                    + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                    + "WHERE s.market = ? ORDER BY s.market, s.symbol, p.timestamp DESC LIMIT ? OFFSET ?";
            if (sort != null && !sort.isEmpty()) {
                query = format("SELECT * FROM (%s) AS data %s", query, getSort(sort));
            }
            String countQuery = "SELECT COUNT(*) FROM ("
                    + "SELECT DISTINCT ON (s.market, s.symbol) s.market, s.symbol FROM stocks s "
                    + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                    + "WHERE s.market = ? ORDER BY s.market, s.symbol, p.timestamp DESC"
                    + ") AS data";
            return results(connection, ROW_MAPPER, page, query, countQuery, market);
        }
    }

    public int consumeForMarket(Connection connection, Market market, Boolean active, Consumer<PricedStock> consumer, Set<Sort> sort) {
        if (active != null) {
            String sql = "SELECT DISTINCT ON (s.market, s.symbol) s.*, p.timestamp, p.price FROM stocks s "
                    + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                    + "WHERE s.market = ? AND s.active = ? ORDER BY s.market, s.symbol, p.timestamp DESC";
            if (sort != null && !sort.isEmpty()) {
                sql = format("SELECT * FROM (%s) AS data %s", sql, getSort(sort));
            }
            return consume(connection, ROW_MAPPER, consumer, sql, market, active);
        } else {
            String sql = "SELECT DISTINCT ON (s.market, s.symbol) s.*, p.timestamp, p.price FROM stocks s "
                    + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                    + "WHERE s.market = ? ORDER BY s.market, s.symbol, p.timestamp DESC";
            if (sort != null && !sort.isEmpty()) {
                sql = format("SELECT * FROM (%s) AS data %s", sql, getSort(sort));
            }
            return consume(connection, ROW_MAPPER, consumer, sql, market);
        }
    }

    public Results<PricedStock> getAll(Connection connection, Boolean active, Page page, Set<Sort> sort) {
        if (active != null) {
            String query = "SELECT DISTINCT ON (s.market, s.symbol) s.*, p.timestamp, p.price FROM stocks s "
                    + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                    + "WHERE active = ? ORDER BY s.market, s.symbol, p.timestamp DESC LIMIT ? OFFSET ?";
            if (sort != null && !sort.isEmpty()) {
                query = format("SELECT * FROM (%s) AS data %s", query, getSort(sort));
            }
            String countQuery = "SELECT COUNT(*) FROM ("
                    + "SELECT DISTINCT ON (s.market, s.symbol) s.market, s.symbol FROM stocks s "
                    + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                    + "WHERE active = ? ORDER BY s.market, s.symbol, p.timestamp DESC"
                    + ") AS data";
            return results(connection, ROW_MAPPER, page, query, countQuery, active);
        } else {
            String query = "SELECT DISTINCT ON (s.market, s.symbol) s.*, p.timestamp, p.price FROM stocks s "
                    + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                    + "ORDER BY s.market, s.symbol, p.timestamp DESC LIMIT ? OFFSET ?";
            if (sort != null && !sort.isEmpty()) {
                query = format("SELECT * FROM (%s) AS data %s", query, getSort(sort));
            }
            String countQuery = "SELECT COUNT(*) FROM ("
                    + "SELECT DISTINCT ON (s.market, s.symbol) s.market, s.symbol FROM stocks s "
                    + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                    + "ORDER BY s.market, s.symbol, p.timestamp DESC"
                    + ") AS data";
            return results(connection, ROW_MAPPER, page, query, countQuery);
        }
    }

    public int consume(Connection connection, Boolean active, Consumer<PricedStock> consumer, Set<Sort> sort) {
        if (active != null) {
            String sql = "SELECT DISTINCT ON (s.market, s.symbol) s.*, p.timestamp, p.price FROM stocks s "
                    + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                    + "WHERE active = ? ORDER BY s.market, s.symbol, p.timestamp DESC";
            if (sort != null && !sort.isEmpty()) {
                sql = format("SELECT * FROM (%s) AS data %s", sql, getSort(sort));
            }
            return consume(connection, ROW_MAPPER, consumer, sql, active);
        } else {
            String sql = "SELECT DISTINCT ON (s.market, s.symbol) s.*, p.timestamp, p.price FROM stocks s "
                    + "LEFT JOIN stock_prices p ON (s.market = p.market AND s.symbol = p.symbol) "
                    + "ORDER BY s.market, s.symbol, p.timestamp DESC";
            if (sort != null && !sort.isEmpty()) {
                sql = format("SELECT * FROM (%s) AS data %s", sql, getSort(sort));
            }
            return consume(connection, ROW_MAPPER, consumer, sql);
        }
    }

    public int add(Connection connection, PricedStock pricedStock) {
        int added = 0;
        added += stockTable.add(connection, pricedStock.asStock());
        added += stockPriceTable.add(connection, pricedStock.asStockPrice());
        return added > 0 ? 1 : 0;
    }
}
