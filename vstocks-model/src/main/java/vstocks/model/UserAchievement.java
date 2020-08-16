package vstocks.model;

import java.time.Instant;
import java.util.Objects;

public class UserAchievement {
    private String userId;
    private String achievementId;
    private Instant timestamp;
    private String description;

    public UserAchievement() {
    }

    public String getUserId() {
        return userId;
    }

    public UserAchievement setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getAchievementId() {
        return achievementId;
    }

    public UserAchievement setAchievementId(String achievementId) {
        this.achievementId = achievementId;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public UserAchievement setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public UserAchievement setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAchievement that = (UserAchievement) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(achievementId, that.achievementId) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, achievementId, timestamp, description);
    }

    @Override
    public String toString() {
        return "UserAchievement{" +
                "userId='" + userId + '\'' +
                ", achievementId='" + achievementId + '\'' +
                ", timestamp=" + timestamp +
                ", description='" + description + '\'' +
                '}';
    }
}
