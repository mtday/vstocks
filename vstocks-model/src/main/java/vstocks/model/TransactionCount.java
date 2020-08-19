package vstocks.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class TransactionCount {
    private Market market;
    private Instant timestamp;
    private long transactions;
    private Map<DeltaInterval, Delta> deltas;

    public TransactionCount() {
    }

    public Market getMarket() {
        return market;
    }

    public TransactionCount setMarket(Market market) {
        this.market = market;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public TransactionCount setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getTransactions() {
        return transactions;
    }

    public TransactionCount setTransactions(long transactions) {
        this.transactions = transactions;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public TransactionCount setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionCount that = (TransactionCount) o;
        return transactions == that.transactions &&
                market == that.market &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(market, timestamp, transactions, deltas);
    }

    @Override
    public String toString() {
        return "TransactionCount{" +
                "market=" + market +
                ", timestamp=" + timestamp +
                ", transactions=" + transactions +
                ", deltas=" + deltas +
                '}';
    }
}
