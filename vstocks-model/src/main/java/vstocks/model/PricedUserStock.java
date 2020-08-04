package vstocks.model;

import java.time.Instant;
import java.util.Objects;

public class PricedUserStock {
    private String userId;
    private Market market;
    private String symbol;
    private int shares;
    private Instant timestamp;
    private int price;

    public PricedUserStock() {
    }

    public UserStock asUserStock() {
        return new UserStock()
                .setUserId(userId)
                .setMarket(market)
                .setSymbol(symbol)
                .setShares(shares);
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

    public int getShares() {
        return shares;
    }

    public PricedUserStock setShares(int shares) {
        this.shares = shares;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public PricedUserStock setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public int getPrice() {
        return price;
    }

    public PricedUserStock setPrice(int price) {
        this.price = price;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PricedUserStock userStock = (PricedUserStock) o;
        return Objects.equals(userId, userStock.userId) &&
                market == userStock.market &&
                Objects.equals(symbol, userStock.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, market.name(), symbol);
    }

    @Override
    public String toString() {
        return "PricedUserStock{" +
                "userId='" + userId + '\'' +
                ", market=" + market +
                ", symbol='" + symbol + '\'' +
                ", shares=" + shares +
                ", timestamp=" + timestamp +
                ", price=" + price +
                '}';
    }
}
