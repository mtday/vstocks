package vstocks.model;

import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class StockPrice {
    private String id;
    private Market market;
    private String stockId;
    private Instant timestamp;
    private int price;

    public StockPrice() {
    }

    public String getId() {
        return id;
    }

    public StockPrice setId(String id) {
        this.id = id;
        return this;
    }

    public Market getMarket() {
        return market;
    }

    public StockPrice setMarket(Market market) {
        this.market = market;
        return this;
    }

    public String getStockId() {
        return stockId;
    }

    public StockPrice setStockId(String stockId) {
        this.stockId = stockId;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public StockPrice setTimestamp(Instant timestamp) {
        this.timestamp = requireNonNull(timestamp);
        return this;
    }

    public int getPrice() {
        return price;
    }

    public StockPrice setPrice(int price) {
        this.price = price;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockPrice stockPrice = (StockPrice) o;
        return Objects.equals(id, stockPrice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SymbolPrice{" +
                "id='" + id + '\'' +
                ", market=" + market +
                ", stockId='" + stockId + '\'' +
                ", timestamp=" + timestamp +
                ", price=" + price +
                '}';
    }
}
