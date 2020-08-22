package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.ActivityLogTable;
import vstocks.db.jdbc.table.UserCountTable;
import vstocks.db.UserDB;
import vstocks.model.*;
import vstocks.model.system.UserCount;
import vstocks.model.system.UserCountCollection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.junit.Assert.*;
import static vstocks.model.ActivityType.USER_LOGIN;
import static vstocks.model.DatabaseField.USERS;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class JdbcUserCountDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private ActivityLogTable activityLogTable;
    private UserDB userTable;
    private UserCountTable userCountTable;

    private JdbcUserCountDB userCountDB;

    private final Instant now = Instant.now().truncatedTo(SECONDS);

    @Before
    public void setup() {
        activityLogTable = new ActivityLogTable();
        userTable = new UserDB();
        userCountTable = new UserCountTable();
        userCountDB = new JdbcUserCountDB(dataSourceExternalResource.get());
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            activityLogTable.truncate(connection);
            userCountTable.truncateTotal(connection);
            userCountTable.truncateActive(connection);
            userTable.truncate(connection);
            connection.commit();
        }
    }

    private Map<DeltaInterval, Delta> getDeltas(int change, float percent) {
        Map<DeltaInterval, Delta> deltas = new TreeMap<>();
        stream(DeltaInterval.values())
                .map(interval -> new Delta().setInterval(interval).setChange(change).setPercent(percent))
                .forEach(delta -> deltas.put(delta.getInterval(), delta));
        return deltas;
    }

    @Test
    public void testGenerateTotalMissing() {
        UserCount userCount = userCountDB.generateTotal();
        assertEquals(0, userCount.getUsers());
    }

    @Test
    public void testGenerateTotalExistsNoUsers() {
        UserCount userCount = userCountDB.generateTotal();
        assertNotNull(userCount.getTimestamp());
        assertEquals(0, userCount.getUsers());
    }

    @Test
    public void testGenerateTotalExistsWithUsers() throws SQLException {
        User user1 = new User().setId(generateId("user1@domain.com")).setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setId(generateId("user2@domain.com")).setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            userTable.add(connection, user1);
            userTable.add(connection, user2);
            connection.commit();
        }

        UserCount fetched = userCountDB.generateTotal();
        assertNotNull(fetched.getTimestamp());
        assertEquals(2, fetched.getUsers());
    }

    @Test
    public void testGenerateActiveMissing() {
        UserCount userCount = userCountDB.generateActive();
        assertEquals(0, userCount.getUsers());
    }

    @Test
    public void testGenerateActiveExistsNoUsers() {
        UserCount userCount = userCountDB.generateActive();
        assertNotNull(userCount.getTimestamp());
        assertEquals(0, userCount.getUsers());
    }

    @Test
    public void testGenerateActiveExistsWithUsers() throws SQLException {
        User user1 = new User().setId(generateId("user1@domain.com")).setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setId(generateId("user2@domain.com")).setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
        ActivityLog activityLog1 = new ActivityLog().setId("1").setUserId(user1.getId()).setType(USER_LOGIN).setTimestamp(now);
        ActivityLog activityLog2 = new ActivityLog().setId("2").setUserId(user2.getId()).setType(USER_LOGIN).setTimestamp(now);

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            userTable.add(connection, user1);
            userTable.add(connection, user2);
            activityLogTable.add(connection, activityLog1);
            activityLogTable.add(connection, activityLog2);
            connection.commit();
        }

        UserCount fetched = userCountDB.generateActive();
        assertNotNull(fetched.getTimestamp());
        assertEquals(2, fetched.getUsers());
    }

    @Test
    public void testGetLatestTotalMissing() {
        UserCountCollection fetched = userCountDB.getLatestTotal();
        assertTrue(fetched.getUserCounts().isEmpty());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }

    @Test
    public void testGetLatestTotalSingleExists() {
        UserCount userCount = new UserCount().setTimestamp(now).setUsers(10);
        assertEquals(1, userCountDB.addTotal(userCount));

        UserCountCollection fetched = userCountDB.getLatestTotal();
        assertEquals(1, fetched.getUserCounts().size());
        assertEquals(userCount, fetched.getUserCounts().iterator().next());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }

    @Test
    public void testGetLatestTotalMultipleExists() {
        UserCount userCount1 = new UserCount().setTimestamp(now).setUsers(10);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(8);
        UserCount userCount3 = new UserCount().setTimestamp(now.minusSeconds(20)).setUsers(6);
        assertEquals(1, userCountDB.addTotal(userCount1));
        assertEquals(1, userCountDB.addTotal(userCount2));
        assertEquals(1, userCountDB.addTotal(userCount3));

        UserCountCollection fetched = userCountDB.getLatestTotal();
        assertEquals(3, fetched.getUserCounts().size());
        assertEquals(userCount1, fetched.getUserCounts().get(0));
        assertEquals(userCount2, fetched.getUserCounts().get(1));
        assertEquals(userCount3, fetched.getUserCounts().get(2));
        assertEquals(getDeltas(4, 66.66667f), fetched.getDeltas());
    }

    @Test
    public void testGetLatestActiveMissing() {
        UserCountCollection fetched = userCountDB.getLatestActive();
        assertTrue(fetched.getUserCounts().isEmpty());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }

    @Test
    public void testGetLatestActiveSingleExists() {
        UserCount userCount = new UserCount().setTimestamp(now).setUsers(10);
        assertEquals(1, userCountDB.addActive(userCount));

        UserCountCollection fetched = userCountDB.getLatestActive();
        assertEquals(1, fetched.getUserCounts().size());
        assertEquals(userCount, fetched.getUserCounts().iterator().next());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }

    @Test
    public void testGetLatestActiveMultipleExists() {
        UserCount userCount1 = new UserCount().setTimestamp(now).setUsers(10);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(8);
        UserCount userCount3 = new UserCount().setTimestamp(now.minusSeconds(20)).setUsers(6);
        assertEquals(1, userCountDB.addActive(userCount1));
        assertEquals(1, userCountDB.addActive(userCount2));
        assertEquals(1, userCountDB.addActive(userCount3));

        UserCountCollection fetched = userCountDB.getLatestActive();
        assertEquals(3, fetched.getUserCounts().size());
        assertEquals(userCount1, fetched.getUserCounts().get(0));
        assertEquals(userCount2, fetched.getUserCounts().get(1));
        assertEquals(userCount3, fetched.getUserCounts().get(2));
        assertEquals(getDeltas(4, 66.66667f), fetched.getDeltas());
    }

    @Test
    public void testGetAllTotalNone() {
        Results<UserCount> results = userCountDB.getAllTotal(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllTotalSomeNoSort() {
        UserCount userCount1 = new UserCount().setTimestamp(now).setUsers(10);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(20);
        assertEquals(1, userCountDB.addTotal(userCount1));
        assertEquals(1, userCountDB.addTotal(userCount2));

        Results<UserCount> results = userCountDB.getAllTotal(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userCount1, results.getResults().get(0));
        assertEquals(userCount2, results.getResults().get(1));
    }

    @Test
    public void testGetAllTotalSomeWithSort() {
        UserCount userCount1 = new UserCount().setTimestamp(now).setUsers(10);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(20);
        assertEquals(1, userCountDB.addTotal(userCount1));
        assertEquals(1, userCountDB.addTotal(userCount2));

        Set<Sort> sort = singleton(USERS.toSort(DESC));
        Results<UserCount> results = userCountDB.getAllTotal(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userCount2, results.getResults().get(0));
        assertEquals(userCount1, results.getResults().get(1));
    }

    @Test
    public void testGetAllActiveNone() {
        Results<UserCount> results = userCountDB.getAllActive(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllActiveSomeNoSort() {
        UserCount userCount1 = new UserCount().setTimestamp(now).setUsers(10);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(20);
        assertEquals(1, userCountDB.addActive(userCount1));
        assertEquals(1, userCountDB.addActive(userCount2));

        Results<UserCount> results = userCountDB.getAllActive(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userCount1, results.getResults().get(0));
        assertEquals(userCount2, results.getResults().get(1));
    }

    @Test
    public void testGetAllActiveSomeWithSort() {
        UserCount userCount1 = new UserCount().setTimestamp(now).setUsers(10);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(20);
        assertEquals(1, userCountDB.addActive(userCount1));
        assertEquals(1, userCountDB.addActive(userCount2));

        Set<Sort> sort = singleton(USERS.toSort(DESC));
        Results<UserCount> results = userCountDB.getAllActive(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userCount2, results.getResults().get(0));
        assertEquals(userCount1, results.getResults().get(1));
    }

    @Test
    public void testAddTotal() {
        UserCount userCount = new UserCount().setTimestamp(now).setUsers(10);
        assertEquals(1, userCountDB.addTotal(userCount));

        UserCountCollection fetched = userCountDB.getLatestTotal();
        assertEquals(1, fetched.getUserCounts().size());
        assertEquals(userCount, fetched.getUserCounts().iterator().next());
    }

    @Test
    public void testAddTotalConflictSameValues() {
        UserCount userCount = new UserCount().setTimestamp(now).setUsers(10);
        assertEquals(1, userCountDB.addTotal(userCount));
        assertEquals(0, userCountDB.addTotal(userCount));

        UserCountCollection fetched = userCountDB.getLatestTotal();
        assertEquals(1, fetched.getUserCounts().size());
        assertEquals(userCount, fetched.getUserCounts().iterator().next());
    }

    @Test
    public void testAddTotalConflictDifferentValues() {
        UserCount userCount = new UserCount().setTimestamp(now).setUsers(10);
        assertEquals(1, userCountDB.addTotal(userCount));
        userCount.setUsers(12);
        assertEquals(1, userCountDB.addTotal(userCount));

        UserCountCollection fetched = userCountDB.getLatestTotal();
        assertEquals(1, fetched.getUserCounts().size());
        assertEquals(userCount, fetched.getUserCounts().iterator().next());
    }

    @Test
    public void testAddActive() {
        UserCount userCount = new UserCount().setTimestamp(now).setUsers(10);
        assertEquals(1, userCountDB.addActive(userCount));

        UserCountCollection fetched = userCountDB.getLatestActive();
        assertEquals(1, fetched.getUserCounts().size());
        assertEquals(userCount, fetched.getUserCounts().iterator().next());
    }

    @Test
    public void testAddActiveConflictSameValues() {
        UserCount userCount = new UserCount().setTimestamp(now).setUsers(10);
        assertEquals(1, userCountDB.addActive(userCount));
        assertEquals(0, userCountDB.addActive(userCount));

        UserCountCollection fetched = userCountDB.getLatestActive();
        assertEquals(1, fetched.getUserCounts().size());
        assertEquals(userCount, fetched.getUserCounts().iterator().next());
    }

    @Test
    public void testAddActiveConflictDifferentValues() {
        UserCount userCount = new UserCount().setTimestamp(now).setUsers(10);
        assertEquals(1, userCountDB.addActive(userCount));
        userCount.setUsers(12);
        assertEquals(1, userCountDB.addActive(userCount));

        UserCountCollection fetched = userCountDB.getLatestActive();
        assertEquals(1, fetched.getUserCounts().size());
        assertEquals(userCount, fetched.getUserCounts().iterator().next());
    }

    @Test
    public void testAgeOffTotal() {
        UserCount userCount1 = new UserCount().setTimestamp(now).setUsers(10);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(10);
        UserCount userCount3 = new UserCount().setTimestamp(now.minusSeconds(20)).setUsers(10);

        assertEquals(1, userCountDB.addTotal(userCount1));
        assertEquals(1, userCountDB.addTotal(userCount2));
        assertEquals(1, userCountDB.addTotal(userCount3));
        assertEquals(2, userCountDB.ageOffTotal(now.minusSeconds(5)));

        Results<UserCount> results = userCountDB.getAllTotal(new Page(), emptySet());
        assertEquals(1, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(userCount1, results.getResults().iterator().next());
    }

    @Test
    public void testAgeOffActive() {
        UserCount userCount1 = new UserCount().setTimestamp(now).setUsers(10);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(10);
        UserCount userCount3 = new UserCount().setTimestamp(now.minusSeconds(20)).setUsers(10);

        assertEquals(1, userCountDB.addActive(userCount1));
        assertEquals(1, userCountDB.addActive(userCount2));
        assertEquals(1, userCountDB.addActive(userCount3));
        assertEquals(2, userCountDB.ageOffActive(now.minusSeconds(5)));

        Results<UserCount> results = userCountDB.getAllActive(new Page(), emptySet());
        assertEquals(1, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(userCount1, results.getResults().iterator().next());
    }
}
