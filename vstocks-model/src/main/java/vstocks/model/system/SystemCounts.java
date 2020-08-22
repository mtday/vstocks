package vstocks.model.system;

import vstocks.model.Market;

import java.util.Map;
import java.util.Objects;

public class SystemCounts {
    private ActiveUserCountCollection activeUserCounts;
    private TotalUserCountCollection totalUserCounts;
    private TotalTransactionCountCollection transactionCounts;
    private Map<Market, TotalMarketTransactionCountCollection> marketTransactionCounts;

    public SystemCounts() {
    }

    public ActiveUserCountCollection getActiveUserCounts() {
        return activeUserCounts;
    }

    public SystemCounts setActiveUserCounts(ActiveUserCountCollection activeUserCounts) {
        this.activeUserCounts = activeUserCounts;
        return this;
    }

    public TotalUserCountCollection getTotalUserCounts() {
        return totalUserCounts;
    }

    public SystemCounts setTotalUserCounts(TotalUserCountCollection totalUserCounts) {
        this.totalUserCounts = totalUserCounts;
        return this;
    }

    public TotalTransactionCountCollection getTransactionCounts() {
        return transactionCounts;
    }

    public SystemCounts setTransactionCounts(TotalTransactionCountCollection transactionCounts) {
        this.transactionCounts = transactionCounts;
        return this;
    }

    public Map<Market, TotalMarketTransactionCountCollection> getMarketTransactionCounts() {
        return marketTransactionCounts;
    }

    public SystemCounts setMarketTransactionCounts(Map<Market, TotalMarketTransactionCountCollection> marketTransactionCounts) {
        this.marketTransactionCounts = marketTransactionCounts;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemCounts that = (SystemCounts) o;
        return Objects.equals(activeUserCounts, that.activeUserCounts) &&
                Objects.equals(totalUserCounts, that.totalUserCounts) &&
                Objects.equals(transactionCounts, that.transactionCounts) &&
                Objects.equals(marketTransactionCounts, that.marketTransactionCounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activeUserCounts, totalUserCounts, transactionCounts, marketTransactionCounts);
    }

    @Override
    public String toString() {
        return "SystemCounts{" +
                "activeUserCounts=" + activeUserCounts +
                ", totalUserCounts=" + totalUserCounts +
                ", transactionCounts=" + transactionCounts +
                ", marketTransactionCounts=" + marketTransactionCounts +
                '}';
    }
}
