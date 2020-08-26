package vstocks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Optional;

public enum Market {
    @JsonProperty("Twitter")
    TWITTER("Twitter"),

    @JsonProperty("YouTube")
    YOUTUBE("YouTube"),

    @JsonProperty("Instagram")
    INSTAGRAM("Instagram"),

    @JsonProperty("Twitch")
    TWITCH("Twitch"),

    @JsonProperty("Facebook")
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
