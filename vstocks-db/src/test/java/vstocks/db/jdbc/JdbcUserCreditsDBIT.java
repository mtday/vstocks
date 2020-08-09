package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.*;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.jdbc.table.UserCreditsTable;
import vstocks.db.jdbc.table.UserTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Sort.SortDirection.DESC;

public class JdbcUserCreditsDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private UserCreditsTable userCreditsTable;
    private JdbcUserCreditsDB userCreditsDB;

    private final User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
    private final User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");

    @Before
    public void setup() throws SQLException {
        userTable = new UserTable();
        userCreditsTable = new UserCreditsTable();
        userCreditsDB = new JdbcUserCreditsDB(dataSourceExternalResource.get());

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            connection.commit();
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            userCreditsTable.truncate(connection);
            userTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() {
        assertFalse(userCreditsDB.get("missing-user").isPresent());
    }

    @Test
    public void testGetExists() {
        UserCredits userCredits = new UserCredits().setUserId(user1.getId()).setCredits(10);
        assertEquals(1, userCreditsDB.add(userCredits));

        Optional<UserCredits> fetched = userCreditsDB.get(userCredits.getUserId());
        assertTrue(fetched.isPresent());
        assertEquals(userCredits.getCredits(), fetched.get().getCredits());
    }

    @Test
    public void testGetAllNone() {
        Results<UserCredits> results = userCreditsDB.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        UserCredits userCredits1 = new UserCredits().setUserId(user1.getId()).setCredits(10);
        UserCredits userCredits2 = new UserCredits().setUserId(user2.getId()).setCredits(10);
        assertEquals(1, userCreditsDB.add(userCredits1));
        assertEquals(1, userCreditsDB.add(userCredits2));

        Results<UserCredits> results = userCreditsDB.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userCredits1, results.getResults().get(0));
        assertEquals(userCredits2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        UserCredits userCredits1 = new UserCredits().setUserId(user1.getId()).setCredits(10);
        UserCredits userCredits2 = new UserCredits().setUserId(user2.getId()).setCredits(10);
        assertEquals(1, userCreditsDB.add(userCredits1));
        assertEquals(1, userCreditsDB.add(userCredits2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), CREDITS.toSort()));
        Results<UserCredits> results = userCreditsDB.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userCredits2, results.getResults().get(0));
        assertEquals(userCredits1, results.getResults().get(1));
    }

    @Test
    public void testConsumeNone() {
        List<UserCredits> list = new ArrayList<>();
        assertEquals(0, userCreditsDB.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        UserCredits userCredits1 = new UserCredits().setUserId(user1.getId()).setCredits(10);
        UserCredits userCredits2 = new UserCredits().setUserId(user2.getId()).setCredits(10);
        assertEquals(1, userCreditsDB.add(userCredits1));
        assertEquals(1, userCreditsDB.add(userCredits2));

        List<UserCredits> list = new ArrayList<>();
        assertEquals(2, userCreditsDB.consume(list::add, emptySet()));
        assertEquals(2, list.size());
        assertEquals(userCredits1, list.get(0));
        assertEquals(userCredits2, list.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        UserCredits userCredits1 = new UserCredits().setUserId(user1.getId()).setCredits(10);
        UserCredits userCredits2 = new UserCredits().setUserId(user2.getId()).setCredits(10);
        assertEquals(1, userCreditsDB.add(userCredits1));
        assertEquals(1, userCreditsDB.add(userCredits2));

        List<UserCredits> list = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), CREDITS.toSort()));
        assertEquals(2, userCreditsDB.consume(list::add, sort));
        assertEquals(2, list.size());
        assertEquals(userCredits2, list.get(0));
        assertEquals(userCredits1, list.get(1));
    }

    @Test
    public void testSetInitialCreditsNoneExists() {
        UserCredits userCredits = new UserCredits().setUserId(user1.getId()).setCredits(10);
        assertEquals(1, userCreditsDB.setInitialCredits(userCredits));

        Optional<UserCredits> fetched = userCreditsDB.get(userCredits.getUserId());
        assertTrue(fetched.isPresent());
        assertEquals(10, fetched.get().getCredits());
    }

    @Test
    public void testSetInitialCreditsAlreadyExists() {
        UserCredits existingCredits = new UserCredits().setUserId(user1.getId()).setCredits(20);
        assertEquals(1, userCreditsDB.setInitialCredits(existingCredits));

        UserCredits userCredits = new UserCredits().setUserId(user1.getId()).setCredits(10);
        assertEquals(0, userCreditsDB.setInitialCredits(userCredits));

        Optional<UserCredits> fetched = userCreditsDB.get(userCredits.getUserId());
        assertTrue(fetched.isPresent());
        assertEquals(20, fetched.get().getCredits());
    }

    @Test
    public void testAdd() {
        UserCredits userCredits = new UserCredits().setUserId(user1.getId()).setCredits(10);
        assertEquals(1, userCreditsDB.add(userCredits));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        UserCredits userCredits = new UserCredits().setUserId(user1.getId()).setCredits(10);
        assertEquals(1, userCreditsDB.add(userCredits));
        userCreditsDB.add(userCredits);
    }

    @Test
    public void testUpdateIncrementMissing() {
        assertEquals(0, userCreditsDB.update("missing-id", 10));
    }

    @Test
    public void testUpdateIncrement() {
        UserCredits userCredits = new UserCredits().setUserId(user1.getId()).setCredits(10);
        assertEquals(1, userCreditsDB.add(userCredits));
        assertEquals(1, userCreditsDB.update(userCredits.getUserId(), 10));

        Optional<UserCredits> updated = userCreditsDB.get(userCredits.getUserId());
        assertTrue(updated.isPresent());
        assertEquals(20, updated.get().getCredits());
    }

    @Test
    public void testUpdateDecrementMissing() {
        assertEquals(0, userCreditsDB.update("missing-id", -10));
    }

    @Test
    public void testUpdateDecrementTooFar() {
        UserCredits userCredits = new UserCredits().setUserId(user1.getId()).setCredits(10);
        assertEquals(1, userCreditsDB.add(userCredits));
        assertEquals(0, userCreditsDB.update(userCredits.getUserId(), -12));

        Optional<UserCredits> fetched = userCreditsDB.get(userCredits.getUserId());
        assertTrue(fetched.isPresent());
        assertEquals(userCredits.getCredits(), fetched.get().getCredits()); // not updated
    }

    @Test
    public void testUpdateDecrement() {
        UserCredits userCredits = new UserCredits().setUserId(user1.getId()).setCredits(10);
        assertEquals(1, userCreditsDB.add(userCredits));
        assertEquals(1, userCreditsDB.update(userCredits.getUserId(), -8));

        Optional<UserCredits> fetched = userCreditsDB.get(userCredits.getUserId());
        assertTrue(fetched.isPresent());
        assertEquals(2, fetched.get().getCredits());
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, userCreditsDB.delete("missing"));
    }

    @Test
    public void testDelete() {
        UserCredits userCredits = new UserCredits().setUserId(user1.getId()).setCredits(10);
        assertEquals(1, userCreditsDB.add(userCredits));
        assertEquals(1, userCreditsDB.delete(userCredits.getUserId()));
        assertFalse(userCreditsDB.get(userCredits.getUserId()).isPresent());
    }
}
