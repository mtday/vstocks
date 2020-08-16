package vstocks.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class Delta {
    private DeltaInterval interval;
    private long change;
    private float percent;

    public Delta() {
    }

    public static <T> Map<DeltaInterval, Delta> getDeltas(List<T> values,
                                                          Function<T, Instant> timestampFn,
                                                          Function<T, Long> valueFn) {
        Map<DeltaInterval, Delta> deltas = new TreeMap<>();
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
                delta.setChange(firstValue - lastValue);
                delta.setPercent((float) (firstValue - lastValue) / lastValue * 100f);
            }
            deltas.put(interval, delta);
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
                interval == delta.interval;
    }

    @Override
    public int hashCode() {
        return Objects.hash(interval, change, percent);
    }

    @Override
    public String toString() {
        return "Delta{" +
                "interval=" + interval +
                ", change=" + change +
                ", percent=" + percent +
                '}';
    }
}
