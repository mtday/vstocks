package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.jdbc.table.ActivityLogTable;
import vstocks.db.jdbc.table.UserAchievementTable;
import vstocks.db.jdbc.table.UserTable;
import vstocks.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static vstocks.model.ActivityType.USER_LOGIN;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Sort.SortDirection.DESC;

public class JdbcUserAchievementDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private ActivityLogTable activityLogTable;
    private UserAchievementTable userAchievementTable;
    private JdbcUserAchievementDB userAchievementDB;

    private final User user1 = new User().setEmail("user1@domain.com").setUsername("user1").setDisplayName("Name1");
    private final User user2 = new User().setEmail("user2@domain.com").setUsername("user2").setDisplayName("Name2");
    private final Achievement achievement1 = new Achievement().setId("id1").setName("Name1");
    private final Achievement achievement2 = new Achievement().setId("id2").setName("Name2");
    private final ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setType(USER_LOGIN).setTimestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS));
    private final ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setType(USER_LOGIN).setTimestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS));

    @Before
    public void setup() throws SQLException {
        userTable = new UserTable();
        activityLogTable = new ActivityLogTable();
        userAchievementTable = new UserAchievementTable();
        userAchievementDB = new JdbcUserAchievementDB(dataSourceExternalResource.get());

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            assertEquals(1, activityLogTable.add(connection, activityLog1));
            assertEquals(1, activityLogTable.add(connection, activityLog2));
            connection.commit();
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            activityLogTable.truncate(connection);
            userAchievementTable.truncate(connection);
            userTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() {
        assertFalse(userAchievementDB.get("missing-id", "missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement = new UserAchievement().setUserId(user1.getId()).setAchievementId(achievement1.getId()).setTimestamp(now).setDescription("Description");
        assertEquals(1, userAchievementDB.add(userAchievement));
        Optional<UserAchievement> fetched = userAchievementDB.get(userAchievement.getUserId(), userAchievement.getAchievementId());
        assertTrue(fetched.isPresent());
        assertEquals(userAchievement.getUserId(), fetched.get().getUserId());
        assertEquals(userAchievement.getAchievementId(), fetched.get().getAchievementId());
        assertEquals(userAchievement.getTimestamp(), fetched.get().getTimestamp());
        assertEquals(userAchievement.getDescription(), fetched.get().getDescription());
    }

    @Test
    public void testGetForUserNone() {
        List<UserAchievement> results = userAchievementDB.getForUser(user1.getId());
        assertTrue(results.isEmpty());
    }

    @Test
    public void testGetForUserSome() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement1 = new UserAchievement().setUserId(user1.getId()).setAchievementId(achievement1.getId()).setTimestamp(now).setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement().setUserId(user1.getId()).setAchievementId(achievement2.getId()).setTimestamp(now).setDescription("Description");
        assertEquals(1, userAchievementDB.add(userAchievement1));
        assertEquals(1, userAchievementDB.add(userAchievement2));
        List<UserAchievement> results = userAchievementDB.getForUser(user1.getId());
        assertEquals(2, results.size());
        assertEquals(userAchievement1, results.get(0));
        assertEquals(userAchievement2, results.get(1));
    }

    @Test
    public void testGetForAchievementNone() {
        Results<UserAchievement> results = userAchievementDB.getForAchievement(achievement1.getId(), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForAchievementSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement1 = new UserAchievement().setUserId(user1.getId()).setAchievementId(achievement1.getId()).setTimestamp(now).setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement().setUserId(user2.getId()).setAchievementId(achievement1.getId()).setTimestamp(now).setDescription("Description");
        assertEquals(1, userAchievementDB.add(userAchievement1));
        assertEquals(1, userAchievementDB.add(userAchievement2));

        Results<UserAchievement> results = userAchievementDB.getForAchievement(achievement1.getId(), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userAchievement1, results.getResults().get(0));
        assertEquals(userAchievement2, results.getResults().get(1));
    }

    @Test
    public void testGetForAchievementSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement1 = new UserAchievement().setUserId(user1.getId()).setAchievementId(achievement1.getId()).setTimestamp(now).setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement().setUserId(user2.getId()).setAchievementId(achievement1.getId()).setTimestamp(now).setDescription("Description");
        assertEquals(1, userAchievementDB.add(userAchievement1));
        assertEquals(1, userAchievementDB.add(userAchievement2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ACHIEVEMENT_ID.toSort()));
        Results<UserAchievement> results = userAchievementDB.getForAchievement(achievement1.getId(), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userAchievement2, results.getResults().get(0));
        assertEquals(userAchievement1, results.getResults().get(1));
    }

    @Test
    public void testGetAllNone() {
        Results<UserAchievement> results = userAchievementDB.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement1 = new UserAchievement().setUserId(user1.getId()).setAchievementId(achievement1.getId()).setTimestamp(now).setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement().setUserId(user2.getId()).setAchievementId(achievement2.getId()).setTimestamp(now).setDescription("Description");
        assertEquals(1, userAchievementDB.add(userAchievement1));
        assertEquals(1, userAchievementDB.add(userAchievement2));

        Results<UserAchievement> results = userAchievementDB.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userAchievement1, results.getResults().get(0));
        assertEquals(userAchievement2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement1 = new UserAchievement().setUserId(user1.getId()).setAchievementId(achievement1.getId()).setTimestamp(now).setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement().setUserId(user2.getId()).setAchievementId(achievement2.getId()).setTimestamp(now).setDescription("Description");
        assertEquals(1, userAchievementDB.add(userAchievement1));
        assertEquals(1, userAchievementDB.add(userAchievement2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ACHIEVEMENT_ID.toSort()));
        Results<UserAchievement> results = userAchievementDB.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userAchievement2, results.getResults().get(0));
        assertEquals(userAchievement1, results.getResults().get(1));
    }

    @Test
    public void testConsumeNone() {
        List<UserAchievement> list = new ArrayList<>();
        assertEquals(0, userAchievementDB.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement1 = new UserAchievement().setUserId(user1.getId()).setAchievementId(achievement1.getId()).setTimestamp(now).setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement().setUserId(user2.getId()).setAchievementId(achievement2.getId()).setTimestamp(now).setDescription("Description");
        assertEquals(1, userAchievementDB.add(userAchievement1));
        assertEquals(1, userAchievementDB.add(userAchievement2));

        List<UserAchievement> list = new ArrayList<>();
        assertEquals(2, userAchievementDB.consume(list::add, emptySet()));
        assertEquals(2, list.size());
        assertEquals(userAchievement1, list.get(0));
        assertEquals(userAchievement2, list.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement1 = new UserAchievement().setUserId(user1.getId()).setAchievementId(achievement1.getId()).setTimestamp(now).setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement().setUserId(user2.getId()).setAchievementId(achievement2.getId()).setTimestamp(now).setDescription("Description");
        assertEquals(1, userAchievementDB.add(userAchievement1));
        assertEquals(1, userAchievementDB.add(userAchievement2));

        List<UserAchievement> list = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ACHIEVEMENT_ID.toSort()));
        assertEquals(2, userAchievementDB.consume(list::add, sort));
        assertEquals(2, list.size());
        assertEquals(userAchievement2, list.get(0));
        assertEquals(userAchievement1, list.get(1));
    }

    @Test
    public void testAdd() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement = new UserAchievement().setUserId(user1.getId()).setAchievementId(achievement1.getId()).setTimestamp(now).setDescription("Description");
        assertEquals(1, userAchievementDB.add(userAchievement));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement = new UserAchievement().setUserId(user1.getId()).setAchievementId(achievement1.getId()).setTimestamp(now).setDescription("Description");
        assertEquals(1, userAchievementDB.add(userAchievement));
        userAchievementDB.add(userAchievement);
    }

    @Test
    public void testDeleteForUser() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement1 = new UserAchievement().setUserId(user1.getId()).setAchievementId(achievement1.getId()).setTimestamp(now).setDescription("Description");
        UserAchievement userAchievement2 = new UserAchievement().setUserId(user2.getId()).setAchievementId(achievement1.getId()).setTimestamp(now).setDescription("Description");

        assertEquals(1, userAchievementDB.add(userAchievement1));
        assertEquals(1, userAchievementDB.add(userAchievement2));
        assertEquals(1, userAchievementDB.deleteForUser(userAchievement1.getUserId()));
        assertTrue(userAchievementDB.getForUser(userAchievement1.getUserId()).isEmpty());
    }

    @Test
    public void testDelete() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserAchievement userAchievement = new UserAchievement().setUserId(user1.getId()).setAchievementId(achievement1.getId()).setTimestamp(now).setDescription("Description");
        assertEquals(1, userAchievementDB.add(userAchievement));
        assertEquals(1, userAchievementDB.delete(userAchievement.getUserId(), userAchievement.getAchievementId()));
        assertFalse(userAchievementDB.get(userAchievement.getUserId(), userAchievement.getAchievementId()).isPresent());
    }
}
