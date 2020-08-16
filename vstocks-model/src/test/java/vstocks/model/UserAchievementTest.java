package vstocks.model;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class UserAchievementTest {
    @Test
    public void testGettersAndSetters() {
        String userId = User.generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        UserAchievement userAchievement = new UserAchievement()
                .setUserId(userId)
                .setAchievementId("achievementId")
                .setTimestamp(now)
                .setDescription("description");

        assertEquals(userId, userAchievement.getUserId());
        assertEquals("achievementId", userAchievement.getAchievementId());
        assertEquals(now, userAchievement.getTimestamp());
        assertEquals("description", userAchievement.getDescription());

        assertEquals(0, UserAchievement.FULL_COMPARATOR.compare(userAchievement, userAchievement));
        assertEquals(0, UserAchievement.UNIQUE_COMPARATOR.compare(userAchievement, userAchievement));
    }

    @Test
    public void testEquals() {
        String userId = User.generateId("user@domain.com");
        UserAchievement userAchievement1 = new UserAchievement().setUserId(userId).setAchievementId("achievementId");
        UserAchievement userAchievement2 = new UserAchievement().setUserId(userId).setAchievementId("achievementId");
        assertEquals(userAchievement1, userAchievement2);
    }

    @Test
    public void testHashCode() {
        String userId = User.generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        UserAchievement userAchievement = new UserAchievement()
                .setUserId(userId)
                .setAchievementId("achievementId")
                .setTimestamp(now)
                .setDescription("description");
        assertEquals(-421194587, userAchievement.hashCode());
    }

    @Test
    public void testToString() {
        String userId = User.generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        UserAchievement userAchievement = new UserAchievement()
                .setUserId(userId)
                .setAchievementId("achievementId")
                .setTimestamp(now)
                .setDescription("description");
        assertEquals("UserAchievement{userId='" + userId + "', achievementId='achievementId', timestamp=" + now
                + ", description='description'}", userAchievement.toString());
    }
}
