package vstocks.model;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;

public enum DeltaInterval {
    HOUR6("6h",   instant -> instant.minusSeconds(HOURS.toSeconds(6)).truncatedTo(SECONDS)),
    HOUR12("12h", instant -> instant.minusSeconds(HOURS.toSeconds(12)).truncatedTo(SECONDS)),
    DAY1("1d",    instant -> instant.minusSeconds(DAYS.toSeconds(1)).truncatedTo(SECONDS)),
    DAY3("3d",    instant -> instant.minusSeconds(DAYS.toSeconds(3)).truncatedTo(SECONDS)),
    DAY7("7d",    instant -> instant.minusSeconds(DAYS.toSeconds(7)).truncatedTo(SECONDS)),
    DAY14("14d",  instant -> instant.minusSeconds(DAYS.toSeconds(14)).truncatedTo(SECONDS)),
    DAY30("30d",  instant -> instant.minusSeconds(DAYS.toSeconds(30)).truncatedTo(SECONDS)),
    ;

    private final String displayName;
    private final Function<Instant, Instant> earliestFn;

    DeltaInterval(String displayName, Function<Instant, Instant> earliestFn) {
        this.displayName = displayName;
        this.earliestFn = earliestFn;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Instant getEarliest() {
        return earliestFn.apply(Instant.now());
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static Optional<DeltaInterval> from(String value) {
        return Arrays.stream(values())
                .filter(interval -> interval.name().equals(value) || interval.getDisplayName().equals(value))
                .findFirst();
    }

    public static DeltaInterval getLast() {
        return DeltaInterval.values()[DeltaInterval.values().length - 1];
    }
}
