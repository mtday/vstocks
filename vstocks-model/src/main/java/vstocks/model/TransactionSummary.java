package vstocks.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class TransactionSummary {
    private Instant timestamp;
    private Map<Market, Long> transactions;
    private long total;
    private Map<DeltaInterval, Delta> deltas;

    public TransactionSummary() {
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public TransactionSummary setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Map<Market, Long> getTransactions() {
        return transactions;
    }

    public TransactionSummary setTransactions(Map<Market, Long> transactions) {
        this.transactions = transactions;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public TransactionSummary setTotal(long total) {
        this.total = total;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public TransactionSummary setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionSummary that = (TransactionSummary) o;
        return total == that.total &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(transactions, that.transactions) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, transactions, total, deltas);
    }

    @Override
    public String toString() {
        return "TransactionSummary{" +
                "timestamp=" + timestamp +
                ", transactions=" + transactions +
                ", total=" + total +
                ", deltas=" + deltas +
                '}';
    }
}
