package vstocks.model;

import java.util.Comparator;
import java.util.Objects;

public class UserStock {
    private String userId;
    private Market market;
    private String symbol;
    private int shares;

    public UserStock() {
    }

    public static final Comparator<UserStock> FULL_COMPARATOR = Comparator
            .comparing(UserStock::getUserId)
            .thenComparing(UserStock::getMarket)
            .thenComparing(UserStock::getSymbol)
            .thenComparingInt(UserStock::getShares);

    public static final Comparator<UserStock> UNIQUE_COMPARATOR = Comparator
            .comparing(UserStock::getUserId)
            .thenComparing(UserStock::getMarket)
            .thenComparing(UserStock::getSymbol);

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

    public int getShares() {
        return shares;
    }

    public UserStock setShares(int shares) {
        this.shares = shares;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserStock userStock = (UserStock) o;
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
        return "UserStock{" +
                "userId='" + userId + '\'' +
                ", market=" + market +
                ", symbol='" + symbol + '\'' +
                ", shares=" + shares +
                '}';
    }
}
