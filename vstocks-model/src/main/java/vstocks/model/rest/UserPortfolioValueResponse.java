package vstocks.model.rest;

import vstocks.model.PortfolioValue;

import java.util.List;

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
}
