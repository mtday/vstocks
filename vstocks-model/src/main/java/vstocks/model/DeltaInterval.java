package vstocks.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;

public enum DeltaInterval {
    @JsonProperty("6h")
    HOUR6("6h",   instant -> instant.minusSeconds(HOURS.toSeconds(6)).truncatedTo(SECONDS)),

    @JsonProperty("12h")
    HOUR12("12h", instant -> instant.minusSeconds(HOURS.toSeconds(12)).truncatedTo(SECONDS)),

    @JsonProperty("1d")
    DAY1("1d",    instant -> instant.minusSeconds(DAYS.toSeconds(1)).truncatedTo(SECONDS)),

    @JsonProperty("3d")
    DAY3("3d",    instant -> instant.minusSeconds(DAYS.toSeconds(3)).truncatedTo(SECONDS)),

    @JsonProperty("7d")
    DAY7("7d",    instant -> instant.minusSeconds(DAYS.toSeconds(7)).truncatedTo(SECONDS)),

    @JsonProperty("14d")
    DAY14("14d",  instant -> instant.minusSeconds(DAYS.toSeconds(14)).truncatedTo(SECONDS)),

    @JsonProperty("30d")
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

    @JsonIgnore
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
