package vstocks.model;

import java.util.Objects;

public class UserStock {
    private String userId;
    private Market market;
    private String symbol;
    private long shares;

    public UserStock() {
    }

    public String getUserId() {
        return userId;
    }

    public UserStock setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Market getMarket() {
        return market;
    }

    public UserStock setMarket(Market market) {
        this.market = market;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public UserStock setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public long getShares() {
        return shares;
    }

    public UserStock setShares(long shares) {
        this.shares = shares;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserStock userStock = (UserStock) o;
        return shares == userStock.shares &&
                Objects.equals(userId, userStock.userId) &&
                market == userStock.market &&
                Objects.equals(symbol, userStock.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, market.name(), symbol, shares);
    }

    @Override
    public String toString() {
        return "UserStock{" +
                "userId='" + userId + '\'' +
                ", market=" + market +
                ", symbol='" + symbol + '\'' +
                ", shares=" + shares +
                '}';
    }
}
