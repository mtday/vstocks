package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.jdbc.table.UserCountTable;
import vstocks.db.jdbc.table.UserTable;
import vstocks.model.*;

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
import static vstocks.model.DatabaseField.USERS;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class JdbcCountDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private UserCountTable userCountTable;

    private JdbcUserCountDB userCountDB;

    private final Instant now = Instant.now().truncatedTo(SECONDS);

    @Before
    public void setup() {
        userTable = new UserTable();
        userCountTable = new UserCountTable();
        userCountDB = new JdbcUserCountDB(dataSourceExternalResource.get());
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            userCountTable.truncate(connection);
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
    public void testGenerateMissing() {
        UserCount userCount = userCountDB.generate();
        assertEquals(0, userCount.getUsers());
        assertNull(userCount.getDeltas());
    }

    @Test
    public void testGenerateExistsNoUsers() {
        UserCount userCount = userCountDB.generate();
        assertNotNull(userCount.getTimestamp());
        assertEquals(0, userCount.getUsers());
        assertNull(userCount.getDeltas());
    }

    @Test
    public void testGenerateExistsWithUsers() throws SQLException {
        User user1 = new User().setId(generateId("user1@domain.com")).setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setId(generateId("user2@domain.com")).setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            userTable.add(connection, user1);
            userTable.add(connection, user2);
            connection.commit();
        }

        UserCount fetched = userCountDB.generate();
        assertNotNull(fetched.getTimestamp());
        assertEquals(2, fetched.getUsers());
        assertNull(fetched.getDeltas());
    }

    @Test
    public void testGetLatestMissing() {
        UserCount fetched = userCountDB.getLatest();
        assertNotNull(fetched.getTimestamp());
        assertEquals(0, fetched.getUsers());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }

    @Test
    public void testGetLatestSingleExists() {
        UserCount userCount = new UserCount().setTimestamp(now).setUsers(10).setDeltas(getDeltas(0, 0f));
        assertEquals(1, userCountDB.add(userCount));

        UserCount fetched = userCountDB.getLatest();
        assertEquals(userCount, fetched);
    }

    @Test
    public void testGetLatestMultipleExists() {
        UserCount userCount1 = new UserCount().setTimestamp(now).setUsers(10).setDeltas(getDeltas(4, 66.66667f));
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(8);
        UserCount userCount3 = new UserCount().setTimestamp(now.minusSeconds(20)).setUsers(6);
        assertEquals(1, userCountDB.add(userCount1));
        assertEquals(1, userCountDB.add(userCount2));
        assertEquals(1, userCountDB.add(userCount3));

        UserCount fetched = userCountDB.getLatest();
        assertEquals(userCount1, fetched);
    }

    @Test
    public void testGetAllNone() {
        Results<UserCount> results = userCountDB.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        UserCount userCount1 = new UserCount().setTimestamp(now).setUsers(10);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(20);
        assertEquals(1, userCountDB.add(userCount1));
        assertEquals(1, userCountDB.add(userCount2));

        Results<UserCount> results = userCountDB.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userCount1, results.getResults().get(0));
        assertEquals(userCount2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        UserCount userCount1 = new UserCount().setTimestamp(now).setUsers(10);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(20);
        assertEquals(1, userCountDB.add(userCount1));
        assertEquals(1, userCountDB.add(userCount2));

        Set<Sort> sort = singleton(USERS.toSort(DESC));
        Results<UserCount> results = userCountDB.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userCount2, results.getResults().get(0));
        assertEquals(userCount1, results.getResults().get(1));
    }

    @Test
    public void testAdd() {
        UserCount userCount = new UserCount().setTimestamp(now).setUsers(10);
        assertEquals(1, userCountDB.add(userCount));
    }

    @Test
    public void testAddConflictSameValues() {
        UserCount userCount = new UserCount().setTimestamp(now).setUsers(10).setDeltas(getDeltas(0, 0f));
        assertEquals(1, userCountDB.add(userCount));
        assertEquals(0, userCountDB.add(userCount));

        UserCount fetched = userCountDB.getLatest();
        assertEquals(userCount, fetched);
    }

    @Test
    public void testAddConflictDifferentValues() {
        UserCount userCount = new UserCount().setTimestamp(now).setUsers(10).setDeltas(getDeltas(0, 0f));
        assertEquals(1, userCountDB.add(userCount));
        userCount.setUsers(12);
        assertEquals(1, userCountDB.add(userCount));

        UserCount fetched = userCountDB.getLatest();
        assertEquals(userCount, fetched);
    }

    @Test
    public void testAgeOff() {
        UserCount userCount1 = new UserCount().setTimestamp(now).setUsers(10);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(10);
        UserCount userCount3 = new UserCount().setTimestamp(now.minusSeconds(20)).setUsers(10);

        assertEquals(1, userCountDB.add(userCount1));
        assertEquals(1, userCountDB.add(userCount2));
        assertEquals(1, userCountDB.add(userCount3));
        assertEquals(2, userCountDB.ageOff(now.minusSeconds(5)));

        Results<UserCount> results = userCountDB.getAll(new Page(), emptySet());
        assertEquals(1, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(userCount1, results.getResults().iterator().next());
    }
}
