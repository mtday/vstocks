package vstocks.model;

import java.time.Instant;
import java.util.Objects;

public class StockActivityLog {
    private String id;
    private String userId;
    private ActivityType type;
    private Instant timestamp;
    private Market market;
    private String symbol;
    private String name;
    private String profileImage;
    private Long shares;
    private Long price;
    private Long value;

    public StockActivityLog() {
    }

    public String getId() {
        return id;
    }

    public StockActivityLog setId(String id) {
        this.id = id;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public StockActivityLog setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public ActivityType getType() {
        return type;
    }

    public StockActivityLog setType(ActivityType type) {
        this.type = type;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public StockActivityLog setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Market getMarket() {
        return market;
    }

    public StockActivityLog setMarket(Market market) {
        this.market = market;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public StockActivityLog setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getName() {
        return name;
    }

    public StockActivityLog setName(String name) {
        this.name = name;
        return this;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public StockActivityLog setProfileImage(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public Long getShares() {
        return shares;
    }

    public StockActivityLog setShares(Long shares) {
        this.shares = shares;
        return this;
    }

    public Long getPrice() {
        return price;
    }

    public StockActivityLog setPrice(Long price) {
        this.price = price;
        return this;
    }

    public Long getValue() {
        return value;
    }

    public StockActivityLog setValue(Long value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockActivityLog that = (StockActivityLog) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                type == that.type &&
                Objects.equals(timestamp, that.timestamp) &&
                market == that.market &&
                Objects.equals(symbol, that.symbol) &&
                Objects.equals(name, that.name) &&
                Objects.equals(profileImage, that.profileImage) &&
                Objects.equals(shares, that.shares) &&
                Objects.equals(price, that.price) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, type.name(), timestamp, market.name(), symbol, name, profileImage, shares,
                price, value);
    }

    @Override
    public String toString() {
        return "StockActivityLog{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", market=" + market +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", shares=" + shares +
                ", price=" + price +
                ", value=" + value +
                '}';
    }
}
