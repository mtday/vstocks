package vstocks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Optional;

public enum AchievementCategory {
    @JsonProperty("Beginner")
    BEGINNER("Beginner"),

    @JsonProperty("Easy")
    EASY("Easy"),

    @JsonProperty("Medium")
    MEDIUM("Medium"),

    @JsonProperty("Hard")
    HARD("Hard"),

    @JsonProperty("Expert")
    EXPERT("Expert"),

    @JsonProperty("Social")
    SOCIAL("Social"),

    @JsonProperty("Challenge")
    CHALLENGE("Challenge"),
    ;

    private final String displayName;

    AchievementCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static Optional<AchievementCategory> from(String value) {
        return Arrays.stream(values())
                .filter(category -> category.name().equals(value) || category.getDisplayName().equals(value))
                .findFirst();
    }
}
