package vstocks.model;

import java.time.Instant;
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
        PricedStock that = (PricedStock) o;
        return price == that.price &&
                market == that.market &&
                Objects.equals(symbol, that.symbol) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(name, that.name) &&
                Objects.equals(profileImage, that.profileImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(market, symbol, timestamp, name, profileImage, price);
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
