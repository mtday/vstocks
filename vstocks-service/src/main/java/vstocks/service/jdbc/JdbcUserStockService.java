package vstocks.service.jdbc;

import vstocks.model.*;
import vstocks.service.UserStockService;
import vstocks.service.jdbc.table.ActivityLogTable;
import vstocks.service.jdbc.table.StockPriceTable;
import vstocks.service.jdbc.table.UserBalanceTable;
import vstocks.service.jdbc.table.UserStockTable;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class JdbcUserStockService extends BaseService implements UserStockService {
    private final UserStockTable userStockTable = new UserStockTable();
    private final UserBalanceTable userBalanceTable = new UserBalanceTable();
    private final StockPriceTable stockPriceTable = new StockPriceTable();
    private final ActivityLogTable activityLogTable = new ActivityLogTable();

    public JdbcUserStockService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<UserStock> get(String userId, String marketId, String stockId) {
        return withConnection(conn -> userStockTable.get(conn, userId, marketId, stockId));
    }

    @Override
    public Results<UserStock> getForUser(String userId, Page page) {
        return withConnection(conn -> userStockTable.getForUser(conn, userId, page));
    }

    @Override
    public Results<UserStock> getForStock(String stockId, Page page) {
        return withConnection(conn -> userStockTable.getForStock(conn, stockId, page));
    }

    @Override
    public Results<UserStock> getAll(Page page) {
        return withConnection(conn -> userStockTable.getAll(conn, page));
    }

    @Override
    public int consume(Consumer<UserStock> consumer) {
        return withConnection(conn -> userStockTable.consume(conn, consumer));
    }

    @Override
    public int buyStock(String userId, String marketId, String stockId, int shares) {
        return withConnection(conn -> {
            Optional<StockPrice> stockPrice = stockPriceTable.getLatest(conn, stockId);
            if (stockPrice.isPresent()) {
                int price = stockPrice.get().getPrice();
                int cost = price * shares;
                if (userBalanceTable.update(conn, userId, -cost) > 0
                        && userStockTable.update(conn, userId, marketId, stockId, shares) > 0) {
                    ActivityLog activityLog = new ActivityLog()
                            .setId(UUID.randomUUID().toString())
                            .setUserId(userId)
                            .setMarketId(marketId)
                            .setStockId(stockId)
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
    public int sellStock(String userId, String marketId, String stockId, int shares) {
        return withConnection(conn -> {
            Optional<StockPrice> stockPrice = stockPriceTable.getLatest(conn, stockId);
            if (stockPrice.isPresent()) {
                int price = stockPrice.get().getPrice();
                int cost = price * shares;
                if (userBalanceTable.update(conn, userId, cost) > 0
                        && userStockTable.update(conn, userId, marketId, stockId, -shares) > 0) {
                    ActivityLog activityLog = new ActivityLog()
                            .setId(UUID.randomUUID().toString())
                            .setUserId(userId)
                            .setMarketId(marketId)
                            .setStockId(stockId)
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
    public int update(String userId, String marketId, String stockId, int delta) {
        return withConnection(conn -> userStockTable.update(conn, userId, marketId, stockId, delta));
    }

    @Override
    public int delete(String userId, String marketId, String stockId) {
        return withConnection(conn -> userStockTable.delete(conn, userId, marketId, stockId));
    }
}
