package vstocks.db.jdbc;

import vstocks.model.*;
import vstocks.db.UserStockDB;
import vstocks.db.jdbc.table.ActivityLogTable;
import vstocks.db.jdbc.table.StockPriceTable;
import vstocks.db.jdbc.table.UserBalanceTable;
import vstocks.db.jdbc.table.UserStockTable;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class JdbcUserStockDB extends BaseService implements UserStockDB {
    private final UserStockTable userStockTable = new UserStockTable();
    private final UserBalanceTable userBalanceTable = new UserBalanceTable();
    private final StockPriceTable stockPriceTable = new StockPriceTable();
    private final ActivityLogTable activityLogTable = new ActivityLogTable();

    public JdbcUserStockDB(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<UserStock> get(String userId, Market market, String symbol) {
        return withConnection(conn -> userStockTable.get(conn, userId, market, symbol));
    }

    @Override
    public Results<UserStock> getForUser(String userId, Page page, Set<Sort> sort) {
        return withConnection(conn -> userStockTable.getForUser(conn, userId, page, sort));
    }

    @Override
    public Results<UserStock> getForStock(Market market, String symbol, Page page, Set<Sort> sort) {
        return withConnection(conn -> userStockTable.getForStock(conn, market, symbol, page, sort));
    }

    @Override
    public Results<UserStock> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> userStockTable.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<UserStock> consumer, Set<Sort> sort) {
        return withConnection(conn -> userStockTable.consume(conn, consumer, sort));
    }

    @Override
    public int buyStock(String userId, Market market, String symbol, int shares) {
        if (shares <= 0) {
            return 0;
        }
        return withConnection(conn -> {
            Optional<StockPrice> stockPrice = stockPriceTable.getLatest(conn, market, symbol);
            if (stockPrice.isPresent()) {
                int price = stockPrice.get().getPrice();
                int cost = price * shares;
                if (userBalanceTable.update(conn, userId, -cost) > 0
                        && userStockTable.update(conn, userId, market, symbol, shares) > 0) {
                    ActivityLog activityLog = new ActivityLog()
                            .setId(UUID.randomUUID().toString())
                            .setUserId(userId)
                            .setMarket(market)
                            .setSymbol(symbol)
                            .setTimestamp(Instant.now())
                            .setPrice(price)
                            .setShares(shares);
                    if (activityLogTable.add(conn, activityLog) > 0) {
                        // everything successful
                        return 1;
                    }
                }
                conn.rollback(); // undo updates
            }
            return 0;
        });
    }

    @Override
    public int sellStock(String userId, Market market, String symbol, int shares) {
        if (shares <= 0) {
            return 0;
        }
        return withConnection(conn -> {
            Optional<StockPrice> stockPrice = stockPriceTable.getLatest(conn, market, symbol);
            if (stockPrice.isPresent()) {
                int price = stockPrice.get().getPrice();
                int cost = price * shares;
                if (userBalanceTable.update(conn, userId, cost) > 0
                        && userStockTable.update(conn, userId, market, symbol, -shares) > 0) {
                    ActivityLog activityLog = new ActivityLog()
                            .setId(UUID.randomUUID().toString())
                            .setUserId(userId)
                            .setMarket(market)
                            .setSymbol(symbol)
                            .setTimestamp(Instant.now())
                            .setPrice(price)
                            .setShares(-shares);
                    if (activityLogTable.add(conn, activityLog) > 0) {
                        // everything successful
                        return 1;
                    }
                }
                conn.rollback(); // undo updates
            }
            return 0;
        });
    }

    @Override
    public int add(UserStock userStock) {
        return withConnection(conn -> userStockTable.add(conn, userStock));
    }

    @Override
    public int update(String userId, Market market, String symbol, int delta) {
        return withConnection(conn -> userStockTable.update(conn, userId, market, symbol, delta));
    }

    @Override
    public int deleteForUser(String userId) {
        return withConnection(conn -> userStockTable.deleteForUser(conn, userId));
    }

    @Override
    public int delete(String userId, Market market, String symbol) {
        return withConnection(conn -> userStockTable.delete(conn, userId, market, symbol));
    }
}
