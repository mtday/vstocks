package vstocks.model;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Stock {
    private String id;
    private String marketId;
    private String symbol;
    private String name;

    public Stock() {
    }

    public String getId() {
        return id;
    }

    public Stock setId(String id) {
        this.id = requireNonNull(id);
        return this;
    }

    public String getMarketId() {
        return marketId;
    }

    public Stock setMarketId(String marketId) {
        this.marketId = marketId;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public Stock setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getName() {
        return name;
    }

    public Stock setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Objects.equals(id, stock.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "id='" + id + '\'' +
                ", marketId='" + marketId + '\'' +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
