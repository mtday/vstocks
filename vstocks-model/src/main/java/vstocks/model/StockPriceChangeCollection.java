package vstocks.model;

import java.util.List;
import java.util.Objects;

public class StockPriceChangeCollection {
    private List<StockPriceChange> changes;

    public StockPriceChangeCollection() {
    }

    public List<StockPriceChange> getChanges() {
        return changes;
    }

    public StockPriceChangeCollection setChanges(List<StockPriceChange> changes) {
        this.changes = changes;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockPriceChangeCollection that = (StockPriceChangeCollection) o;
        return Objects.equals(changes, that.changes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(changes);
    }

    @Override
    public String toString() {
        return "StockPriceChangeCollection{" +
                "changes=" + changes +
                '}';
    }
}
