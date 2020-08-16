package vstocks.model.rest;

import vstocks.model.Delta;
import vstocks.model.DeltaInterval;
import vstocks.model.PortfolioValue;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserPortfolioValueResponse {
    private PortfolioValue currentValue;
    private List<PortfolioValue> historicalValues;
    private Map<DeltaInterval, Delta> deltas;

    public UserPortfolioValueResponse() {
    }

    public PortfolioValue getCurrentValue() {
        return currentValue;
    }

    public UserPortfolioValueResponse setCurrentValue(PortfolioValue currentValue) {
        this.currentValue = currentValue;
        return this;
    }

    public List<PortfolioValue> getHistoricalValues() {
        return historicalValues;
    }

    public UserPortfolioValueResponse setHistoricalValues(List<PortfolioValue> historicalValues) {
        this.historicalValues = historicalValues;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public UserPortfolioValueResponse setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPortfolioValueResponse that = (UserPortfolioValueResponse) o;
        return Objects.equals(currentValue, that.currentValue) &&
                Objects.equals(historicalValues, that.historicalValues) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentValue, historicalValues, deltas);
    }

    @Override
    public String toString() {
        return "UserPortfolioValueResponse{" +
                "currentValue=" + currentValue +
                ", historicalValues=" + historicalValues +
                ", deltas=" + deltas +
                '}';
    }
}
