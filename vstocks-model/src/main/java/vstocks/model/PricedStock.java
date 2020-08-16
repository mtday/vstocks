package vstocks.model;

import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;

public class PricedStock {
    private Market market;
    private String symbol;
    private Instant timestamp;
    private String name;
    private String profileImage;
    private int price;

    public PricedStock() {
    }

    public static final Comparator<PricedStock> FULL_COMPARATOR = Comparator
            .comparing(PricedStock::getMarket)
            .thenComparing(PricedStock::getSymbol)
            .thenComparing(PricedStock::getTimestamp)
            .thenComparing(PricedStock::getName)
            .thenComparing(PricedStock::getProfileImage)
            .thenComparingInt(PricedStock::getPrice);

    public static final Comparator<PricedStock> UNIQUE_COMPARATOR = Comparator
            .comparing(PricedStock::getMarket)
            .thenComparing(PricedStock::getSymbol)
            .thenComparing(PricedStock::getTimestamp);

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

    public Instant getTimestamp() {
        return timestamp;
    }

    public PricedStock setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getName() {
        return name;
    }

    public PricedStock setName(String name) {
        this.name = name;
        return this;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public PricedStock setProfileImage(String profileImage) {
        this.profileImage = profileImage;
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
                Objects.equals(symbol, stock.symbol) &&
                Objects.equals(timestamp, stock.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(market.name(), symbol, timestamp);
    }

    @Override
    public String toString() {
        return "PricedStock{" +
                "market=" + market +
                ", symbol='" + symbol + '\'' +
                ", timestamp=" + timestamp +
                ", name='" + name + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", price=" + price +
                '}';
    }
}
