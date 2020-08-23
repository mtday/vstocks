package vstocks.model;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static vstocks.model.User.generateId;

public class UserAchievementTest {
    private final String userId = generateId("user@domain.com");
    private final Instant now = Instant.now().truncatedTo(SECONDS);

    @Test
    public void testGettersAndSetters() {
        UserAchievement userAchievement = new UserAchievement()
                .setUserId(userId)
                .setAchievementId("achievementId")
                .setTimestamp(now)
                .setDescription("description");

        assertEquals(userId, userAchievement.getUserId());
        assertEquals("achievementId", userAchievement.getAchievementId());
        assertEquals(now, userAchievement.getTimestamp());
        assertEquals("description", userAchievement.getDescription());
    }

    @Test
    public void testEquals() {
        UserAchievement userAchievement1 = new UserAchievement().setUserId(userId).setAchievementId("achievementId");
        UserAchievement userAchievement2 = new UserAchievement().setUserId(userId).setAchievementId("achievementId");
        assertEquals(userAchievement1, userAchievement2);
    }

    @Test
    public void testHashCode() {
        UserAchievement userAchievement = new UserAchievement()
                .setUserId(generateId("user@domain.com"))
                .setAchievementId("achievementId")
                .setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z"))
                .setDescription("description");
        assertEquals(923521, new UserAchievement().hashCode());
        assertEquals(-502597578, userAchievement.hashCode());
    }

    @Test
    public void testToString() {
        UserAchievement userAchievement = new UserAchievement()
                .setUserId(userId)
                .setAchievementId("achievementId")
                .setTimestamp(now)
                .setDescription("description");
        assertEquals("UserAchievement{userId='" + userId + "', achievementId='achievementId', timestamp=" + now
                + ", description='description'}", userAchievement.toString());
    }
}
