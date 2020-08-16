package vstocks.model.rest;

import vstocks.model.PortfolioValue;

import java.util.List;
import java.util.Objects;

public class UserPortfolioValueResponse {
    private PortfolioValue currentValue;
    private List<PortfolioValue> historicalValues;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPortfolioValueResponse that = (UserPortfolioValueResponse) o;
        return Objects.equals(currentValue, that.currentValue) &&
                Objects.equals(historicalValues, that.historicalValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentValue, historicalValues);
    }

    @Override
    public String toString() {
        return "UserPortfolioValueResponse{" +
                "currentValue=" + currentValue +
                ", historicalValues=" + historicalValues +
                '}';
    }
}
