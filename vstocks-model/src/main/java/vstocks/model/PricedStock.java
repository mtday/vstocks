package vstocks.model;

import java.time.Instant;
import java.util.Objects;

public class PricedStock {
    private Market market;
    private String symbol;
    private String name;
    private boolean active = true;
    private Instant timestamp;
    private int price;

    public PricedStock() {
    }

    public Stock asStock() {
        return new Stock()
                .setMarket(market)
                .setSymbol(symbol)
                .setName(name)
                .setActive(active);
    }

    public StockPrice asStockPrice() {
        return new StockPrice()
                .setMarket(market)
                .setSymbol(symbol)
                .setTimestamp(timestamp)
                .setPrice(price);
    }

    public Market getMarket() {
        return market;
    }

    public PricedStock setMarket(Market market) {
        this.market = market;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public PricedStock setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getName() {
        return name;
    }

    public PricedStock setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public PricedStock setActive(boolean active) {
        this.active = active;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public PricedStock setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public int getPrice() {
        return price;
    }

    public PricedStock setPrice(int price) {
        this.price = price;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PricedStock stock = (PricedStock) o;
        return market == stock.market &&
                Objects.equals(symbol, stock.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(market.name(), symbol);
    }

    @Override
    public String toString() {
        return "PricedStock{" +
                "market=" + market +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", timestamp=" + timestamp +
                ", price=" + price +
                '}';
    }
}