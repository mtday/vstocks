package vstocks.model.system;

import vstocks.model.Market;

import java.util.Map;
import java.util.Objects;

public class SystemCounts {
    private ActiveUserCountCollection activeUserCounts;
    private TotalUserCountCollection totalUserCounts;
    private ActiveTransactionCountCollection activeTransactionCounts;
    private TotalTransactionCountCollection totalTransactionCounts;
    private Map<Market, ActiveMarketTransactionCountCollection> activeMarketTransactionCounts;
    private Map<Market, TotalMarketTransactionCountCollection> totalMarketTransactionCounts;

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

    public ActiveTransactionCountCollection getActiveTransactionCounts() {
        return activeTransactionCounts;
    }

    public SystemCounts setActiveTransactionCounts(ActiveTransactionCountCollection activeTransactionCounts) {
        this.activeTransactionCounts = activeTransactionCounts;
        return this;
    }

    public TotalTransactionCountCollection getTotalTransactionCounts() {
        return totalTransactionCounts;
    }

    public SystemCounts setTotalTransactionCounts(TotalTransactionCountCollection totalTransactionCounts) {
        this.totalTransactionCounts = totalTransactionCounts;
        return this;
    }

    public Map<Market, ActiveMarketTransactionCountCollection> getActiveMarketTransactionCounts() {
        return activeMarketTransactionCounts;
    }

    public SystemCounts setActiveMarketTransactionCounts(Map<Market, ActiveMarketTransactionCountCollection> activeMarketTransactionCounts) {
        this.activeMarketTransactionCounts = activeMarketTransactionCounts;
        return this;
    }

    public Map<Market, TotalMarketTransactionCountCollection> getTotalMarketTransactionCounts() {
        return totalMarketTransactionCounts;
    }

    public SystemCounts setTotalMarketTransactionCounts(Map<Market, TotalMarketTransactionCountCollection> totalMarketTransactionCounts) {
        this.totalMarketTransactionCounts = totalMarketTransactionCounts;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemCounts that = (SystemCounts) o;
        return Objects.equals(activeUserCounts, that.activeUserCounts) &&
                Objects.equals(totalUserCounts, that.totalUserCounts) &&
                Objects.equals(activeTransactionCounts, that.activeTransactionCounts) &&
                Objects.equals(totalTransactionCounts, that.totalTransactionCounts) &&
                Objects.equals(activeMarketTransactionCounts, that.activeMarketTransactionCounts) &&
                Objects.equals(totalMarketTransactionCounts, that.totalMarketTransactionCounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activeUserCounts, totalUserCounts, activeTransactionCounts, totalTransactionCounts, activeMarketTransactionCounts, totalMarketTransactionCounts);
    }

    @Override
    public String toString() {
        return "SystemCounts{" +
                "activeUserCounts=" + activeUserCounts +
                ", totalUserCounts=" + totalUserCounts +
                ", activeTransactionCounts=" + activeTransactionCounts +
                ", totalTransactionCounts=" + totalTransactionCounts +
                ", activeMarketTransactionCounts=" + activeMarketTransactionCounts +
                ", totalMarketTransactionCounts=" + totalMarketTransactionCounts +
                '}';
    }
}
