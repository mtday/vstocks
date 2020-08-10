package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public class UserAchievementTest {
    @Test
    public void testGettersAndSetters() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
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
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        UserAchievement userAchievement1 = new UserAchievement().setUserId(userId).setAchievementId("achievementId");
        UserAchievement userAchievement2 = new UserAchievement().setUserId(userId).setAchievementId("achievementId");
        assertEquals(userAchievement1, userAchievement2);
    }

    @Test
    public void testHashCode() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement = new UserAchievement()
                .setUserId(userId)
                .setAchievementId("achievementId")
                .setTimestamp(now)
                .setDescription("description");
        assertEquals(-421194587, userAchievement.hashCode());
    }

    @Test
    public void testToString() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement = new UserAchievement()
                .setUserId(userId)
                .setAchievementId("achievementId")
                .setTimestamp(now)
                .setDescription("description");
        assertEquals("UserAchievement{userId='" + userId + "', achievementId='achievementId', timestamp=" + now
                + ", description='description'}", userAchievement.toString());
    }
}
