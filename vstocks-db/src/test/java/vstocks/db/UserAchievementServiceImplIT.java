package vstocks.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.model.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static vstocks.model.ActivityType.USER_LOGIN;
import static vstocks.model.DatabaseField.ACHIEVEMENT_ID;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class UserAchievementServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private ActivityLogService activityLogService;
    private UserAchievementService userAchievementService;

    private final User user1 = new User()
            .setId(generateId("user1@domain.com"))
            .setEmail("user1@domain.com")
            .setUsername("user1")
            .setDisplayName("Name1")
            .setProfileImage("link1");
    private final User user2 = new User()
            .setId(generateId("user2@domain.com"))
            .setEmail("user2@domain.com")
            .setUsername("user2")
            .setDisplayName("Name2")
            .setProfileImage("link1");

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

    private final UserAchievement userAchievement11 = new UserAchievement()
            .setUserId(user1.getId())
            .setAchievementId(achievement1.getId())
            .setTimestamp(now)
            .setDescription("Description");
    private final UserAchievement userAchievement12 = new UserAchievement()
            .setUserId(user1.getId())
            .setAchievementId(achievement2.getId())
            .setTimestamp(now.minusSeconds(10))
            .setDescription("Description");
    private final UserAchievement userAchievement21 = new UserAchievement()
            .setUserId(user2.getId())
            .setAchievementId(achievement1.getId())
            .setTimestamp(now)
            .setDescription("Description");
    private final UserAchievement userAchievement22 = new UserAchievement()
            .setUserId(user2.getId())
            .setAchievementId(achievement2.getId())
            .setTimestamp(now.minusSeconds(10))
            .setDescription("Description");

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
        assertEquals(1, userAchievementService.add(userAchievement11));

        UserAchievement fetched = userAchievementService.get(user1.getId(), achievement1.getId()).orElse(null);
        assertEquals(userAchievement11, fetched);
    }

    @Test
    public void testGetForUserNone() {
        List<UserAchievement> results = userAchievementService.getForUser(user1.getId());
        validateResults(results);
    }

    @Test
    public void testGetForUserSome() {
        assertEquals(1, userAchievementService.add(userAchievement11));
        assertEquals(1, userAchievementService.add(userAchievement12));

        List<UserAchievement> results = userAchievementService.getForUser(user1.getId());
        validateResults(results, userAchievement11, userAchievement12);
    }

    @Test
    public void testGetForAchievementNone() {
        Results<UserAchievement> results =
                userAchievementService.getForAchievement(achievement1.getId(), new Page(), emptySet());
        validateResults(results);
    }

    @Test
    public void testGetForAchievementSomeNoSort() {
        assertEquals(1, userAchievementService.add(userAchievement11));
        assertEquals(1, userAchievementService.add(userAchievement21));

        Results<UserAchievement> results =
                userAchievementService.getForAchievement(achievement1.getId(), new Page(), emptySet());
        validateResults(results, userAchievement11, userAchievement21);
    }

    @Test
    public void testGetForAchievementSomeWithSort() {
        assertEquals(1, userAchievementService.add(userAchievement11));
        assertEquals(1, userAchievementService.add(userAchievement21));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ACHIEVEMENT_ID.toSort()));
        Results<UserAchievement> results =
                userAchievementService.getForAchievement(achievement1.getId(), new Page(), sort);
        validateResults(results, userAchievement21, userAchievement11);
    }

    @Test
    public void testGetAllNone() {
        Results<UserAchievement> results = userAchievementService.getAll(new Page(), emptySet());
        validateResults(results);
    }

    @Test
    public void testGetAllSomeNoSort() {
        assertEquals(1, userAchievementService.add(userAchievement11));
        assertEquals(1, userAchievementService.add(userAchievement21));

        Results<UserAchievement> results = userAchievementService.getAll(new Page(), emptySet());
        validateResults(results, userAchievement11, userAchievement21);
    }

    @Test
    public void testGetAllSomeWithSort() {
        assertEquals(1, userAchievementService.add(userAchievement11));
        assertEquals(1, userAchievementService.add(userAchievement21));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ACHIEVEMENT_ID.toSort()));
        Results<UserAchievement> results = userAchievementService.getAll(new Page(), sort);
        validateResults(results, userAchievement21, userAchievement11);
    }

    @Test
    public void testConsumeNone() {
        List<UserAchievement> results = new ArrayList<>();
        assertEquals(0, userAchievementService.consume(results::add, emptySet()));
        validateResults(results);
    }

    @Test
    public void testConsumeSomeNoSort() {
        assertEquals(1, userAchievementService.add(userAchievement11));
        assertEquals(1, userAchievementService.add(userAchievement22));

        List<UserAchievement> results = new ArrayList<>();
        assertEquals(2, userAchievementService.consume(results::add, emptySet()));
        validateResults(results, userAchievement11, userAchievement22);
    }

    @Test
    public void testConsumeSomeWithSort() {
        assertEquals(1, userAchievementService.add(userAchievement11));
        assertEquals(1, userAchievementService.add(userAchievement22));

        List<UserAchievement> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(ACHIEVEMENT_ID.toSort(DESC), USER_ID.toSort()));
        assertEquals(2, userAchievementService.consume(results::add, sort));
        validateResults(results, userAchievement22, userAchievement11);
    }

    @Test
    public void testAdd() {
        assertEquals(1, userAchievementService.add(userAchievement11));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        assertEquals(1, userAchievementService.add(userAchievement11));
        userAchievementService.add(userAchievement11);
    }

    @Test
    public void testDeleteForUser() {
        assertEquals(1, userAchievementService.add(userAchievement11));
        assertEquals(1, userAchievementService.add(userAchievement21));

        assertEquals(1, userAchievementService.deleteForUser(userAchievement11.getUserId()));
        assertTrue(userAchievementService.getForUser(userAchievement11.getUserId()).isEmpty());
    }

    @Test
    public void testDelete() {
        assertEquals(1, userAchievementService.add(userAchievement11));
        assertEquals(1, userAchievementService.delete(user1.getId(), achievement1.getId()));
        assertFalse(userAchievementService.get(user1.getId(), achievement1.getId()).isPresent());
    }

    @Test
    public void testTruncate() {
        assertEquals(1, userAchievementService.add(userAchievement11));
        assertEquals(1, userAchievementService.add(userAchievement12));
        assertEquals(1, userAchievementService.add(userAchievement21));
        assertEquals(1, userAchievementService.add(userAchievement22));

        assertEquals(4, userAchievementService.truncate());

        Results<UserAchievement> results = userAchievementService.getAll(new Page(), emptySet());
        validateResults(results);
    }
}
