package vstocks.model;

import java.util.Objects;

public class Stock {
    private Market market;
    private String symbol;
    private String name;

    public Stock() {
    }

    public Market getMarket() {
        return market;
    }

    public Stock setMarket(Market market) {
        this.market = market;
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
        return market == stock.market &&
                Objects.equals(symbol, stock.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(market.name(), symbol);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "market=" + market +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
