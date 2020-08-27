package vstocks.model;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class Delta {
    private DeltaInterval interval;
    private Long oldest;
    private Long newest;
    private long change;
    private float percent;

    public Delta() {
    }

    public static <T> List<Delta> getDeltas(List<T> values,
                                            Function<T, Instant> timestampFn,
                                            Function<T, Long> valueFn) {
        List<Delta> deltas = new ArrayList<>(DeltaInterval.values().length);
        for (DeltaInterval interval : DeltaInterval.values()) {
            Instant earliest = interval.getEarliest();
            List<T> sinceEarliest = values.stream()
                    .filter(t -> !timestampFn.apply(t).isBefore(earliest))
                    .collect(toList());

            T first = sinceEarliest.isEmpty() ? null : sinceEarliest.get(0);
            T last = sinceEarliest.isEmpty() ? null : sinceEarliest.get(sinceEarliest.size() - 1);

            Delta delta = new Delta().setInterval(interval).setChange(0).setPercent(0);
            if (first != null && last != null) {
                long firstValue = valueFn.apply(first);
                long lastValue = valueFn.apply(last);
                delta.setNewest(firstValue); // values are expected to be sorted with newest first
                delta.setOldest(lastValue);
                delta.setChange(firstValue - lastValue);
                // We do this == comparison to prevent floating point precision errors in the tests
                delta.setPercent(firstValue == lastValue ? 0 : (float) (firstValue - lastValue) / lastValue * 100f);
            }
            deltas.add(delta);
        }
        return deltas;
    }

    // Flips the "positive" on the values, for example with ranks, lower is better going from 4 to 1 should be shown
    // as a positive change.
    public static <T> List<Delta> getReverseDeltas(List<T> values,
                                                   Function<T, Instant> timestampFn,
                                                   Function<T, Long> valueFn) {
        List<Delta> deltas = new ArrayList<>(DeltaInterval.values().length);
        for (DeltaInterval interval : DeltaInterval.values()) {
            Instant earliest = interval.getEarliest();
            List<T> sinceEarliest = values.stream()
                    .filter(t -> !timestampFn.apply(t).isBefore(earliest))
                    .collect(toList());

            T first = sinceEarliest.isEmpty() ? null : sinceEarliest.get(0);
            T last = sinceEarliest.isEmpty() ? null : sinceEarliest.get(sinceEarliest.size() - 1);

            Delta delta = new Delta().setInterval(interval).setChange(0).setPercent(0);
            if (first != null && last != null) {
                long firstValue = valueFn.apply(first); // negate the values
                long lastValue = valueFn.apply(last);
                delta.setNewest(firstValue); // values are expected to be sorted with newest first
                delta.setOldest(lastValue);
                delta.setChange(lastValue - firstValue);
                // We do this == comparison to prevent floating point precision errors in the tests
                delta.setPercent(firstValue == lastValue ? 0 : (float) (lastValue - firstValue) / lastValue * 100f);
            }
            deltas.add(delta);
        }
        return deltas;
    }

    public DeltaInterval getInterval() {
        return interval;
    }

    public Delta setInterval(DeltaInterval interval) {
        this.interval = interval;
        return this;
    }

    public Long getOldest() {
        return oldest;
    }

    public Delta setOldest(Long oldest) {
        this.oldest = oldest;
        return this;
    }

    public Long getNewest() {
        return newest;
    }

    public Delta setNewest(Long newest) {
        this.newest = newest;
        return this;
    }

    public long getChange() {
        return change;
    }

    public Delta setChange(long change) {
        this.change = change;
        return this;
    }

    public float getPercent() {
        return percent;
    }

    public Delta setPercent(float percent) {
        this.percent = percent;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Delta delta = (Delta) o;
        return change == delta.change &&
                Float.compare(delta.percent, percent) == 0 &&
                interval == delta.interval &&
                Objects.equals(oldest, delta.oldest) &&
                Objects.equals(newest, delta.newest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interval, oldest, newest, change, percent);
    }

    @Override
    public String toString() {
        return "Delta{" +
                "interval=" + interval +
                ", oldest=" + oldest +
                ", newest=" + newest +
                ", change=" + change +
                ", percent=" + percent +
                '}';
    }
}
