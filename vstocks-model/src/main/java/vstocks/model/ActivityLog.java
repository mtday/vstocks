package vstocks.model;

import java.time.Instant;
import java.util.Objects;

public class ActivityLog {
    private String id;
    private String userId;
    private ActivityType type;
    private Instant timestamp;
    private Market market;
    private String symbol;
    private Integer shares;
    private Integer price;

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

    public ActivityType getType() {
        return type;
    }

    public ActivityLog setType(ActivityType type) {
        this.type = type;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public ActivityLog setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Market getMarket() {
        return market;
    }

    public ActivityLog setMarket(Market market) {
        this.market = market;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public ActivityLog setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public Integer getShares() {
        return shares;
    }

    public ActivityLog setShares(Integer shares) {
        this.shares = shares;
        return this;
    }

    public Integer getPrice() {
        return price;
    }

    public ActivityLog setPrice(Integer price) {
        this.price = price;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityLog that = (ActivityLog) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                type == that.type &&
                Objects.equals(timestamp, that.timestamp) &&
                market == that.market &&
                Objects.equals(symbol, that.symbol) &&
                Objects.equals(shares, that.shares) &&
                Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, type, timestamp, market, symbol, shares, price);
    }

    @Override
    public String toString() {
        return "ActivityLog{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", market=" + market +
                ", symbol='" + symbol + '\'' +
                ", shares=" + shares +
                ", price=" + price +
                '}';
    }
}
