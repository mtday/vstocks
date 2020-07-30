package vstocks.model;

import java.time.Instant;
import java.util.Objects;

public class ActivityLog {
    private String id;
    private String userId;
    private Market market;
    private String stockId;
    private Instant timestamp;
    private int shares;
    private int price;

    public ActivityLog() {
    }

    public String getId() {
        return id;
    }

    public ActivityLog setId(String id) {
        this.id = id;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public ActivityLog setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Market getMarket() {
        return market;
    }

    public ActivityLog setMarket(Market market) {
        this.market = market;
        return this;
    }

    public String getStockId() {
        return stockId;
    }

    public ActivityLog setStockId(String stockId) {
        this.stockId = stockId;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public ActivityLog setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public int getShares() {
        return shares;
    }

    public ActivityLog setShares(int shares) {
        this.shares = shares;
        return this;
    }

    public int getPrice() {
        return price;
    }

    public ActivityLog setPrice(int price) {
        this.price = price;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityLog activityLog = (ActivityLog) o;
        return Objects.equals(id, activityLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ActivityLog{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", market=" + market +
                ", stockId='" + stockId + '\'' +
                ", timestamp=" + timestamp +
                ", shares=" + shares +
                ", price=" + price +
                '}';
    }
}
