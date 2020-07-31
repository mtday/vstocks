package vstocks.model;

import java.util.Arrays;
import java.util.Optional;

public enum Market {
    TWITTER("Twitter"),
    YOUTUBE("YouTube"),
    INSTAGRAM("Instagram");

    private String displayName;

    Market(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Market setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public static Optional<Market> from(String id) {
        return Arrays.stream(values())
                .filter(market -> market.name().equalsIgnoreCase(id) || market.getDisplayName().equalsIgnoreCase(id))
                .findFirst();
    }
}
