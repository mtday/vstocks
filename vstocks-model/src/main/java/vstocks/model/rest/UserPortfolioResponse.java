package vstocks.model.rest;

import vstocks.model.PortfolioValue;

import java.util.List;

public class UserPortfolioResponse {
    private PortfolioValue currentValue;
    private List<PortfolioValue> historicalValues;

    public UserPortfolioResponse() {
    }

    public PortfolioValue getCurrentValue() {
        return currentValue;
    }

    public UserPortfolioResponse setCurrentValue(PortfolioValue currentValue) {
        this.currentValue = currentValue;
        return this;
    }

    public List<PortfolioValue> getHistoricalValues() {
        return historicalValues;
    }

    public UserPortfolioResponse setHistoricalValues(List<PortfolioValue> historicalValues) {
        this.historicalValues = historicalValues;
        return this;
    }
}
