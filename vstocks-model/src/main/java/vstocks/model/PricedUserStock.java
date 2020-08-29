package vstocks.model;

import java.time.Instant;
import java.util.Objects;

public class PricedUserStock {
    private String userId;
    private Market market;
    private String symbol;
    private String name;
    private String profileImage;
    private Instant timestamp;
    private long shares;
    private long price;
    private long value;

    public PricedUserStock() {
    }

    public UserStock asUserStock() {
        return new UserStock()
                .setUserId(userId)
                .setMarket(market)
                .setSymbol(symbol)
                .setShares(shares);
    }

    public Stock asStock() {
        return new Stock()
                .setMarket(market)
                .setSymbol(symbol)
                .setName(name)
                .setProfileImage(profileImage);
    }

    public StockPrice asStockPrice() {
        return new StockPrice()
                .setMarket(market)
                .setSymbol(symbol)
                .setTimestamp(timestamp)
                .setPrice(price);
    }

    public String getUserId() {
        return userId;
    }

    public PricedUserStock setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Market getMarket() {
        return market;
    }

    public PricedUserStock setMarket(Market market) {
        this.market = market;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public PricedUserStock setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public PricedUserStock setName(String name) {
        this.name = name;
        return this;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public PricedUserStock setProfileImage(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public PricedUserStock setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getShares() {
        return shares;
    }

    public PricedUserStock setShares(long shares) {
        this.shares = shares;
        return this;
    }

    public long getPrice() {
        return price;
    }

    public PricedUserStock setPrice(long price) {
        this.price = price;
        return this;
    }

    public long getValue() {
        return value;
    }

    public PricedUserStock setValue(long value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PricedUserStock that = (PricedUserStock) o;
        return shares == that.shares &&
                price == that.price &&
                value == that.value &&
                Objects.equals(userId, that.userId) &&
                market == that.market &&
                Objects.equals(symbol, that.symbol) &&
                Objects.equals(name, that.name) &&
                Objects.equals(profileImage, that.profileImage) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, market.name(), symbol, name, profileImage, timestamp, shares, price, value);
    }

    @Override
    public String toString() {
        return "PricedUserStock{" +
                "userId='" + userId + '\'' +
                ", market=" + market +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", timestamp=" + timestamp +
                ", shares=" + shares +
                ", price=" + price +
                ", value=" + value +
                '}';
    }
}
