package vstocks.model;

import java.util.Objects;

public class UserStock {
    private String userId;
    private Market market;
    private String stockId;
    private int shares;

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

    public String getStockId() {
        return stockId;
    }

    public UserStock setStockId(String stockId) {
        this.stockId = stockId;
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
                Objects.equals(stockId, userStock.stockId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, market, stockId);
    }

    @Override
    public String toString() {
        return "UserStock{" +
                "userId='" + userId + '\'' +
                ", market=" + market +
                ", stockId='" + stockId + '\'' +
                ", shares=" + shares +
                '}';
    }
}
