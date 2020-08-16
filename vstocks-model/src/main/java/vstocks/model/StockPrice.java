package vstocks.model;

import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class StockPrice {
    private Market market;
    private String symbol;
    private Instant timestamp;
    private int price;

    public StockPrice() {
    }

    public Market getMarket() {
        return market;
    }

    public StockPrice setMarket(Market market) {
        this.market = market;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public StockPrice setSymbol(String symbol) {
        this.symbol = symbol;
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
        StockPrice that = (StockPrice) o;
        return price == that.price &&
                market == that.market &&
                Objects.equals(symbol, that.symbol) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(market, symbol, timestamp, price);
    }

    @Override
    public String toString() {
        return "StockPrice{" +
                "market=" + market +
                ", symbol='" + symbol + '\'' +
                ", timestamp=" + timestamp +
                ", price=" + price +
                '}';
    }
}
