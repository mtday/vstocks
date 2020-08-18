package vstocks.model;

import java.util.Arrays;
import java.util.Optional;

public enum Market {
    TWITTER("Twitter"),
    YOUTUBE("YouTube"),
    INSTAGRAM("Instagram"),
    TWITCH("Twitch"),
    FACEBOOK("Facebook");

    private final String displayName;

    Market(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static Optional<Market> from(String name) {
        return Arrays.stream(values())
                .filter(market -> market.name().equalsIgnoreCase(name) || market.getDisplayName().equalsIgnoreCase(name))
                .findFirst();
    }
}
