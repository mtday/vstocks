package vstocks.model;

import java.util.Objects;

public class UserStock {
    private String userId;
    private String marketId;
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

    public String getMarketId() {
        return marketId;
    }

    public UserStock setMarketId(String marketId) {
        this.marketId = marketId;
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
                Objects.equals(marketId, userStock.marketId) &&
                Objects.equals(stockId, userStock.stockId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, marketId, stockId);
    }

    @Override
    public String toString() {
        return "UserStock{" +
                "userId='" + userId + '\'' +
                ", marketId='" + marketId + '\'' +
                ", stockId='" + stockId + '\'' +
                ", shares=" + shares +
                '}';
    }
}
