package vstocks.model;

import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class StockPriceChange {
    private long batch;
    private Market market;
    private String symbol;
    private Instant timestamp;
    private long price;
    private long change;
    private float percent;

    public StockPriceChange() {
    }

    public long getBatch() {
        return batch;
    }

    public StockPriceChange setBatch(long batch) {
        this.batch = batch;
        return this;
    }

    public Market getMarket() {
        return market;
    }

    public StockPriceChange setMarket(Market market) {
        this.market = market;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public StockPriceChange setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public StockPriceChange setTimestamp(Instant timestamp) {
        this.timestamp = requireNonNull(timestamp);
        return this;
    }

    public long getPrice() {
        return price;
    }

    public StockPriceChange setPrice(long price) {
        this.price = price;
        return this;
    }

    public long getChange() {
        return change;
    }

    public StockPriceChange setChange(long change) {
        this.change = change;
        return this;
    }

    public float getPercent() {
        return percent;
    }

    public StockPriceChange setPercent(float percent) {
        this.percent = percent;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockPriceChange that = (StockPriceChange) o;
        return batch == that.batch &&
                price == that.price &&
                change == that.change &&
                Float.compare(that.percent, percent) == 0 &&
                market == that.market &&
                Objects.equals(symbol, that.symbol) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batch, market.name(), symbol, timestamp, price, change, percent);
    }

    @Override
    public String toString() {
        return "StockPriceChange{" +
                "batch=" + batch +
                ", market=" + market +
                ", symbol='" + symbol + '\'' +
                ", timestamp=" + timestamp +
                ", price=" + price +
                ", change=" + change +
                ", percent=" + percent +
                '}';
    }
}
