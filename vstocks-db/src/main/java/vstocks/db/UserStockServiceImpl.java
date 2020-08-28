package vstocks.db;

import vstocks.model.*;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static vstocks.model.ActivityType.STOCK_BUY;
import static vstocks.model.ActivityType.STOCK_SELL;

public class UserStockServiceImpl extends BaseService implements UserStockService {
    private final UserStockDB userStockDB = new UserStockDB();
    private final UserCreditsDB userCreditsDB = new UserCreditsDB();
    private final StockPriceDB stockPriceDB = new StockPriceDB();
    private final ActivityLogDB activityLogDB = new ActivityLogDB();

    public UserStockServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<UserStock> get(String userId, Market market, String symbol) {
        return withConnection(conn -> userStockDB.get(conn, userId, market, symbol));
    }

    @Override
    public Results<UserStock> getForUser(String userId, Page page, List<Sort> sort) {
        return withConnection(conn -> userStockDB.getForUser(conn, userId, page, sort));
    }

    @Override
    public Results<UserStock> getForStock(Market market, String symbol, Page page, List<Sort> sort) {
        return withConnection(conn -> userStockDB.getForStock(conn, market, symbol, page, sort));
    }

    @Override
    public Results<UserStock> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> userStockDB.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<UserStock> consumer, List<Sort> sort) {
        return withConnection(conn -> userStockDB.consume(conn, consumer, sort));
    }

    @Override
    public int buyStock(String userId, Market market, String symbol, long shares) {
        if (shares <= 0) {
            return 0;
        }
        return withConnection(conn -> {
            Optional<StockPrice> stockPrice = stockPriceDB.getLatest(conn, market, symbol);
            if (stockPrice.isPresent()) {
                long price = stockPrice.get().getPrice();
                long cost = price * shares;
                if (userCreditsDB.update(conn, userId, -cost) > 0
                        && userStockDB.update(conn, userId, market, symbol, shares) > 0) {
                    ActivityLog activityLog = new ActivityLog()
                            .setId(UUID.randomUUID().toString())
                            .setUserId(userId)
                            .setType(STOCK_BUY)
                            .setTimestamp(Instant.now())
                            .setMarket(market)
                            .setSymbol(symbol)
                            .setPrice(price)
                            .setShares(shares)
                            .setValue(price * shares);
                    if (activityLogDB.add(conn, activityLog) > 0) {
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
    public int sellStock(String userId, Market market, String symbol, long shares) {
        if (shares <= 0) {
            return 0;
        }
        return withConnection(conn -> {
            Optional<StockPrice> stockPrice = stockPriceDB.getLatest(conn, market, symbol);
            if (stockPrice.isPresent()) {
                long price = stockPrice.get().getPrice();
                long cost = price * shares;
                if (userCreditsDB.update(conn, userId, cost) > 0
                        && userStockDB.update(conn, userId, market, symbol, -shares) > 0) {
                    ActivityLog activityLog = new ActivityLog()
                            .setId(UUID.randomUUID().toString())
                            .setUserId(userId)
                            .setType(STOCK_SELL)
                            .setTimestamp(Instant.now())
                            .setMarket(market)
                            .setSymbol(symbol)
                            .setPrice(price)
                            .setShares(-shares)
                            .setValue(price * -shares);
                    if (activityLogDB.add(conn, activityLog) > 0) {
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
        return withConnection(conn -> userStockDB.add(conn, userStock));
    }

    @Override
    public int update(String userId, Market market, String symbol, long delta) {
        return withConnection(conn -> userStockDB.update(conn, userId, market, symbol, delta));
    }

    @Override
    public int deleteForUser(String userId) {
        return withConnection(conn -> userStockDB.deleteForUser(conn, userId));
    }

    @Override
    public int delete(String userId, Market market, String symbol) {
        return withConnection(conn -> userStockDB.delete(conn, userId, market, symbol));
    }

    @Override
    public int truncate() {
        return withConnection(userStockDB::truncate);
    }
}
