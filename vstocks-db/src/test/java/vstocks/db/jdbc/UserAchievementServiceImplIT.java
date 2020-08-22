package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static vstocks.model.ActivityType.USER_LOGIN;
import static vstocks.model.DatabaseField.ACHIEVEMENT_ID;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class UserAchievementServiceImplIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserService userService;
    private ActivityLogService activityLogService;
    private UserAchievementService userAchievementService;

    private final Instant now = Instant.now().truncatedTo(SECONDS);

    private final User user1 = new User()
            .setId(generateId("user1@domain.com"))
            .setEmail("user1@domain.com")
            .setUsername("user1")
            .setDisplayName("Name1");
    private final User user2 = new User()
            .setId(generateId("user2@domain.com"))
            .setEmail("user2@domain.com")
            .setUsername("user2")
            .setDisplayName("Name2");
    private final Achievement achievement1 = new Achievement().setId("id1").setName("Name1");
    private final Achievement achievement2 = new Achievement().setId("id2").setName("Name2");
    private final ActivityLog activityLog1 = new ActivityLog()
            .setId("id1")
            .setUserId(user1.getId())
            .setType(USER_LOGIN)
            .setTimestamp(now);
    private final ActivityLog activityLog2 = new ActivityLog()
            .setId("id2")
            .setUserId(user2.getId())
            .setType(USER_LOGIN)
            .setTimestamp(now);

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        activityLogService = new ActivityLogServiceImpl(dataSourceExternalResource.get());
        userAchievementService = new UserAchievementServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));
    }

    @After
    public void cleanup() {
        activityLogService.truncate();
        userAchievementService.truncate();
        userService.truncate();
    }

    @Test
    public void testGetMissing() {
        assertFalse(userAchievementService.get("missing-id", "missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        UserAchievement userAchievement = new UserAchievement()
                .setUserId(user1.getId())
                .setAchievementId(achievement1.getId())
                .setTimestamp(now)
                .setDescription("Description");
        assertEquals(1, userAchievementService.add(userAchievement));

        UserAchievement fetched = userAchievementService.get(user1.getId(), achievement1.getId()).orElse(null);
        assertEquals(userAchievement, fetched);
    }

    @Test
    public void testGetForUserNone() {
        List<UserAchievement> results = userAchievementService.getForUser(user1.getId());
        assertTrue(results.isEmpty());
    }

    @Test
    public void testGetForUserSome() {
        UserAchievement userAchievement1 = new UserAchievement()
                .setUserId(user1.getId())
                .setAchievementId(achievement1.getId())
                .setTimestamp(now)
                .setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement()
                .setUserId(user1.getId())
                .setAchievementId(achievement2.getId())
                .setTimestamp(now)
                .setDescription("Description");
        assertEquals(1, userAchievementService.add(userAchievement1));
        assertEquals(1, userAchievementService.add(userAchievement2));

        List<UserAchievement> results = userAchievementService.getForUser(user1.getId());
        assertEquals(2, results.size());
        assertEquals(userAchievement1, results.get(0));
        assertEquals(userAchievement2, results.get(1));
    }

    @Test
    public void testGetForAchievementNone() {
        Results<UserAchievement> results = userAchievementService.getForAchievement(achievement1.getId(), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForAchievementSomeNoSort() {
        UserAchievement userAchievement1 = new UserAchievement()
                .setUserId(user1.getId())
                .setAchievementId(achievement1.getId())
                .setTimestamp(now)
                .setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement()
                .setUserId(user2.getId())
                .setAchievementId(achievement1.getId())
                .setTimestamp(now)
                .setDescription("Description");
        assertEquals(1, userAchievementService.add(userAchievement1));
        assertEquals(1, userAchievementService.add(userAchievement2));

        Results<UserAchievement> results = userAchievementService.getForAchievement(achievement1.getId(), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userAchievement1, results.getResults().get(0));
        assertEquals(userAchievement2, results.getResults().get(1));
    }

    @Test
    public void testGetForAchievementSomeWithSort() {
        UserAchievement userAchievement1 = new UserAchievement()
                .setUserId(user1.getId())
                .setAchievementId(achievement1.getId())
                .setTimestamp(now)
                .setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement()
                .setUserId(user2.getId())
                .setAchievementId(achievement1.getId())
                .setTimestamp(now)
                .setDescription("Description");
        assertEquals(1, userAchievementService.add(userAchievement1));
        assertEquals(1, userAchievementService.add(userAchievement2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ACHIEVEMENT_ID.toSort()));
        Results<UserAchievement> results = userAchievementService.getForAchievement(achievement1.getId(), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userAchievement2, results.getResults().get(0));
        assertEquals(userAchievement1, results.getResults().get(1));
    }

    @Test
    public void testGetAllNone() {
        Results<UserAchievement> results = userAchievementService.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        UserAchievement userAchievement1 = new UserAchievement()
                .setUserId(user1.getId())
                .setAchievementId(achievement1.getId())
                .setTimestamp(now)
                .setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement()
                .setUserId(user2.getId())
                .setAchievementId(achievement2.getId())
                .setTimestamp(now)
                .setDescription("Description");
        assertEquals(1, userAchievementService.add(userAchievement1));
        assertEquals(1, userAchievementService.add(userAchievement2));

        Results<UserAchievement> results = userAchievementService.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userAchievement1, results.getResults().get(0));
        assertEquals(userAchievement2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        UserAchievement userAchievement1 = new UserAchievement()
                .setUserId(user1.getId())
                .setAchievementId(achievement1.getId())
                .setTimestamp(now)
                .setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement()
                .setUserId(user2.getId())
                .setAchievementId(achievement2.getId())
                .setTimestamp(now)
                .setDescription("Description");
        assertEquals(1, userAchievementService.add(userAchievement1));
        assertEquals(1, userAchievementService.add(userAchievement2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ACHIEVEMENT_ID.toSort()));
        Results<UserAchievement> results = userAchievementService.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userAchievement2, results.getResults().get(0));
        assertEquals(userAchievement1, results.getResults().get(1));
    }

    @Test
    public void testConsumeNone() {
        List<UserAchievement> list = new ArrayList<>();
        assertEquals(0, userAchievementService.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        UserAchievement userAchievement1 = new UserAchievement()
                .setUserId(user1.getId())
                .setAchievementId(achievement1.getId())
                .setTimestamp(now)
                .setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement()
                .setUserId(user2.getId())
                .setAchievementId(achievement2.getId())
                .setTimestamp(now)
                .setDescription("Description");
        assertEquals(1, userAchievementService.add(userAchievement1));
        assertEquals(1, userAchievementService.add(userAchievement2));

        List<UserAchievement> results = new ArrayList<>();
        assertEquals(2, userAchievementService.consume(results::add, emptySet()));
        assertEquals(2, results.size());
        assertEquals(userAchievement1, results.get(0));
        assertEquals(userAchievement2, results.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        UserAchievement userAchievement1 = new UserAchievement()
                .setUserId(user1.getId())
                .setAchievementId(achievement1.getId())
                .setTimestamp(now)
                .setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement()
                .setUserId(user2.getId())
                .setAchievementId(achievement2.getId())
                .setTimestamp(now)
                .setDescription("Description");
        assertEquals(1, userAchievementService.add(userAchievement1));
        assertEquals(1, userAchievementService.add(userAchievement2));

        List<UserAchievement> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ACHIEVEMENT_ID.toSort()));
        assertEquals(2, userAchievementService.consume(results::add, sort));
        assertEquals(2, results.size());
        assertEquals(userAchievement2, results.get(0));
        assertEquals(userAchievement1, results.get(1));
    }

    @Test
    public void testAdd() {
        UserAchievement userAchievement = new UserAchievement()
                .setUserId(user1.getId())
                .setAchievementId(achievement1.getId())
                .setTimestamp(now)
                .setDescription("Description");
        assertEquals(1, userAchievementService.add(userAchievement));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        UserAchievement userAchievement = new UserAchievement()
                .setUserId(user1.getId())
                .setAchievementId(achievement1.getId())
                .setTimestamp(now)
                .setDescription("Description");
        assertEquals(1, userAchievementService.add(userAchievement));
        userAchievementService.add(userAchievement);
    }

    @Test
    public void testDeleteForUser() {
        UserAchievement userAchievement1 = new UserAchievement()
                .setUserId(user1.getId())
                .setAchievementId(achievement1.getId())
                .setTimestamp(now)
                .setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement()
                .setUserId(user2.getId())
                .setAchievementId(achievement1.getId())
                .setTimestamp(now)
                .setDescription("Description");

        assertEquals(1, userAchievementService.add(userAchievement1));
        assertEquals(1, userAchievementService.add(userAchievement2));
        assertEquals(1, userAchievementService.deleteForUser(userAchievement1.getUserId()));
        assertTrue(userAchievementService.getForUser(userAchievement1.getUserId()).isEmpty());
    }

    @Test
    public void testDelete() {
        UserAchievement userAchievement = new UserAchievement()
                .setUserId(user1.getId())
                .setAchievementId(achievement1.getId())
                .setTimestamp(now)
                .setDescription("Description");
        assertEquals(1, userAchievementService.add(userAchievement));
        assertEquals(1, userAchievementService.delete(userAchievement.getUserId(), userAchievement.getAchievementId()));
        assertFalse(userAchievementService.get(userAchievement.getUserId(), userAchievement.getAchievementId()).isPresent());
    }
}
