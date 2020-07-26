package vstocks.db.service.impl;

import vstocks.db.service.UserStockService;
import vstocks.db.store.ActivityLogStore;
import vstocks.db.store.StockPriceStore;
import vstocks.db.store.UserBalanceStore;
import vstocks.db.store.UserStockStore;
import vstocks.model.*;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class DefaultUserStockService extends BaseService implements UserStockService {
    private final UserStockStore userStockStore;
    private final UserBalanceStore userBalanceStore;
    private final StockPriceStore stockPriceStore;
    private final ActivityLogStore activityLogStore;

    public DefaultUserStockService(DataSource dataSource,
                                   UserStockStore userStockStore,
                                   UserBalanceStore userBalanceStore,
                                   StockPriceStore stockPriceStore,
                                   ActivityLogStore activityLogStore) {
        super(dataSource);
        this.userStockStore = userStockStore;
        this.userBalanceStore = userBalanceStore;
        this.stockPriceStore = stockPriceStore;
        this.activityLogStore = activityLogStore;
    }

    @Override
    public Optional<UserStock> get(String userId, String marketId, String stockId) {
        return withConnection(conn -> userStockStore.get(conn, userId, marketId, stockId));
    }

    @Override
    public Results<UserStock> getForUser(String userId, Page page) {
        return withConnection(conn -> userStockStore.getForUser(conn, userId, page));
    }

    @Override
    public Results<UserStock> getForStock(String stockId, Page page) {
        return withConnection(conn -> userStockStore.getForStock(conn, stockId, page));
    }

    @Override
    public Results<UserStock> getAll(Page page) {
        return withConnection(conn -> userStockStore.getAll(conn, page));
    }

    @Override
    public int buyStock(String userId, String marketId, String stockId, int shares) {
        return withConnection(conn -> {
            Optional<StockPrice> stockPrice = stockPriceStore.getLatest(conn, stockId);
            if (stockPrice.isPresent()) {
                int price = stockPrice.get().getPrice();
                int cost = price * shares;
                if (userBalanceStore.update(conn, userId, -cost) > 0
                        && userStockStore.update(conn, userId, marketId, stockId, shares) > 0) {
                    ActivityLog activityLog = new ActivityLog()
                            .setId(UUID.randomUUID().toString())
                            .setUserId(userId)
                            .setMarketId(marketId)
                            .setStockId(stockId)
                            .setTimestamp(Instant.now())
                            .setPrice(price)
                            .setShares(shares);
                    if (activityLogStore.add(conn, activityLog) > 0) {
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
            Optional<StockPrice> stockPrice = stockPriceStore.getLatest(conn, stockId);
            if (stockPrice.isPresent()) {
                int price = stockPrice.get().getPrice();
                int cost = price * shares;
                if (userBalanceStore.update(conn, userId, cost) > 0
                        && userStockStore.update(conn, userId, marketId, stockId, -shares) > 0) {
                    ActivityLog activityLog = new ActivityLog()
                            .setId(UUID.randomUUID().toString())
                            .setUserId(userId)
                            .setMarketId(marketId)
                            .setStockId(stockId)
                            .setTimestamp(Instant.now())
                            .setPrice(price)
                            .setShares(-shares);
                    if (activityLogStore.add(conn, activityLog) > 0) {
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
        return withConnection(conn -> userStockStore.add(conn, userStock));
    }

    @Override
    public int update(String userId, String marketId, String stockId, int delta) {
        return withConnection(conn -> userStockStore.update(conn, userId, marketId, stockId, delta));
    }

    @Override
    public int delete(String userId, String marketId, String stockId) {
        return withConnection(conn -> userStockStore.delete(conn, userId, marketId, stockId));
    }
}
