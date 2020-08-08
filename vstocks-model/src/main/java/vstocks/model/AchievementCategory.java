package vstocks.model;

import java.util.Arrays;
import java.util.Optional;

public enum AchievementCategory {
    BEGINNER("Beginner"),
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
    EXPERT("Expert"),
    SOCIAL("Social"),
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
