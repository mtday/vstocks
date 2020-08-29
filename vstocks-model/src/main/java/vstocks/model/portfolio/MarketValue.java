package vstocks.model.portfolio;

import vstocks.model.Market;

import java.util.Objects;

public class MarketValue {
    private Market market;
    private long value;

    public MarketValue() {
    }

    public Market getMarket() {
        return market;
    }

    public MarketValue setMarket(Market market) {
        this.market = market;
        return this;
    }

    public long getValue() {
        return value;
    }

    public MarketValue setValue(long value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketValue that = (MarketValue) o;
        return value == that.value &&
                market == that.market;
    }

    @Override
    public int hashCode() {
        return Objects.hash(market.name(), value);
    }

    @Override
    public String toString() {
        return "MarketValue{" +
                "market=" + market +
                ", value=" + value +
                '}';
    }
}
