package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;

public class UserAchievementTest {
    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement = new UserAchievement()
                .setUserId("userId")
                .setAchievementId("achievementId")
                .setTimestamp(now)
                .setDescription("description");

        assertEquals("userId", userAchievement.getUserId());
        assertEquals("achievementId", userAchievement.getAchievementId());
        assertEquals(now, userAchievement.getTimestamp());
        assertEquals("description", userAchievement.getDescription());
    }

    @Test
    public void testEquals() {
        UserAchievement userAchievement1 = new UserAchievement().setUserId("user").setAchievementId("achievementId");
        UserAchievement userAchievement2 = new UserAchievement().setUserId("user").setAchievementId("achievementId");
        assertEquals(userAchievement1, userAchievement2);
    }

    @Test
    public void testHashCode() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement = new UserAchievement()
                .setUserId("userId")
                .setAchievementId("achievementId")
                .setTimestamp(now)
                .setDescription("description");
        assertEquals(-16898331, userAchievement.hashCode());
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement = new UserAchievement()
                .setUserId("userId")
                .setAchievementId("achievementId")
                .setTimestamp(now)
                .setDescription("description");
        assertEquals("UserAchievement{userId='userId', achievementId='achievementId', timestamp=" + now
                + ", description='description'}", userAchievement.toString());
    }
}
