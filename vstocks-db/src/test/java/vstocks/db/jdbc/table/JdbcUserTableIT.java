package vstocks.db.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.User;
import vstocks.db.DataSourceExternalResource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Locale.ENGLISH;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Sort.SortDirection.DESC;

public class JdbcUserTableIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;

    @Before
    public void setup() {
        userTable = new UserTable();
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            userTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testUsernameExistsFalse() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userTable.usernameExists(connection, "missing"));
        }
    }

    @Test
    public void testUsernameExists() throws SQLException {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertTrue(userTable.usernameExists(connection, user.getUsername()));
        }
    }

    @Test
    public void testGetMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userTable.get(connection, "missing-id").isPresent());
        }
    }

    @Test
    public void testGetExists() throws SQLException {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<User> fetched = userTable.get(connection, user.getId());
            assertTrue(fetched.isPresent());
            assertEquals(user.getId(), fetched.get().getId());
            assertEquals(user.getEmail(), fetched.get().getEmail());
            assertEquals(user.getUsername(), fetched.get().getUsername());
            assertEquals(user.getDisplayName(), fetched.get().getDisplayName());
        }
    }

    @Test
    public void testGetExistsEmailLowercase() throws SQLException {
        User user = new User().setEmail("USER@DOMAIN.COM").setUsername("name").setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<User> fetched = userTable.get(connection, user.getId());
            assertTrue(fetched.isPresent());
            assertEquals(user.getId(), fetched.get().getId());
            assertEquals(user.getEmail().toLowerCase(ENGLISH), fetched.get().getEmail()); // email automatically lower-cased
            assertEquals(user.getUsername(), fetched.get().getUsername());
            assertEquals(user.getDisplayName(), fetched.get().getDisplayName());
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<User> results =  userTable.getAll(connection, new Page(), emptySet());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSomeNoSort() throws SQLException {
        User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<User> results = userTable.getAll(connection, new Page(), emptySet());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(user1, results.getResults().get(0));
            assertEquals(user2, results.getResults().get(1));
        }
    }

    @Test
    public void testGetAllSomeWithSort() throws SQLException {
        User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Set<Sort> sort = new LinkedHashSet<>(asList(USERNAME.toSort(DESC), DISPLAY_NAME.toSort()));
            Results<User> results = userTable.getAll(connection, new Page(), sort);
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(user2, results.getResults().get(0));
            assertEquals(user1, results.getResults().get(1));
        }
    }

    @Test
    public void testGetAllMultiplePagesNoSort() throws SQLException {
        User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
        User user3 = new User().setEmail("user3@domain.com").setUsername("name3").setDisplayName("Name3");
        User user4 = new User().setEmail("user4@domain.com").setUsername("name4").setDisplayName("Name4");
        User user5 = new User().setEmail("user5@domain.com").setUsername("name5").setDisplayName("Name5");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            for (User user : asList(user1, user2, user3, user4, user5)) {
                assertEquals(1, userTable.add(connection, user));
            }
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Page page = new Page().setSize(2);
            Results<User> results = userTable.getAll(connection, page, emptySet());
            assertEquals(5, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(user1, results.getResults().get(0));
            assertEquals(user2, results.getResults().get(1));

            page = page.next();
            results = userTable.getAll(connection, page, emptySet());
            assertEquals(5, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(user3, results.getResults().get(0));
            assertEquals(user4, results.getResults().get(1));

            page = page.next();
            results = userTable.getAll(connection, page, emptySet());
            assertEquals(5, results.getTotal());
            assertEquals(1, results.getResults().size());
            assertEquals(user5, results.getResults().get(0));
        }
    }

    @Test
    public void testGetAllMultiplePagesWithSort() throws SQLException {
        User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
        User user3 = new User().setEmail("user3@domain.com").setUsername("name3").setDisplayName("Name3");
        User user4 = new User().setEmail("user4@domain.com").setUsername("name4").setDisplayName("Name4");
        User user5 = new User().setEmail("user5@domain.com").setUsername("name5").setDisplayName("Name5");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            for (User user : asList(user1, user2, user3, user4, user5)) {
                assertEquals(1, userTable.add(connection, user));
            }
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Page page = new Page().setSize(2);
            Set<Sort> sort = new LinkedHashSet<>(asList(USERNAME.toSort(DESC), DISPLAY_NAME.toSort()));
            Results<User> results = userTable.getAll(connection, page, sort);
            assertEquals(5, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(user5, results.getResults().get(0));
            assertEquals(user4, results.getResults().get(1));

            page = page.next();
            results = userTable.getAll(connection, page, sort);
            assertEquals(5, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(user3, results.getResults().get(0));
            assertEquals(user2, results.getResults().get(1));

            page = page.next();
            results = userTable.getAll(connection, page, sort);
            assertEquals(5, results.getTotal());
            assertEquals(1, results.getResults().size());
            assertEquals(user1, results.getResults().get(0));
        }
    }

    @Test
    public void testConsumeNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<User> list = new ArrayList<>();
            assertEquals(0, userTable.consume(connection, list::add, emptySet()));
            assertTrue(list.isEmpty());
        }
    }

    @Test
    public void testConsumeSomeNoSort() throws SQLException {
        User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<User> list = new ArrayList<>();
            assertEquals(2, userTable.consume(connection, list::add, emptySet()));
            assertEquals(2, list.size());
            assertEquals(user1, list.get(0));
            assertEquals(user2, list.get(1));
        }
    }

    @Test
    public void testConsumeSomeWithSort() throws SQLException {
        User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<User> list = new ArrayList<>();
            Set<Sort> sort = new LinkedHashSet<>(asList(USERNAME.toSort(DESC), DISPLAY_NAME.toSort()));
            assertEquals(2, userTable.consume(connection, list::add, sort));
            assertEquals(2, list.size());
            assertEquals(user2, list.get(0));
            assertEquals(user1, list.get(1));
        }
    }

    @Test
    public void testAdd() throws SQLException {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user));
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddIdConflict() throws SQLException {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user));
            user.setUsername("different");
            userTable.add(connection, user);
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddUsernameConflict() throws SQLException {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user));
            user.setId("different");
            userTable.add(connection, user);
            connection.commit();
        }
    }

    @Test
    public void testUpdateMissing() throws SQLException {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userTable.update(connection, user));
        }
    }

    @Test
    public void testUpdateNoChange() throws SQLException {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userTable.update(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<User> updated = userTable.get(connection, user.getId());
            assertTrue(updated.isPresent());
            assertEquals(user.getId(), updated.get().getId());
            assertEquals(user.getEmail(), updated.get().getEmail());
            assertEquals(user.getUsername(), updated.get().getUsername());
            assertEquals(user.getDisplayName(), updated.get().getDisplayName());
        }
    }

    @Test
    public void testUpdate() throws SQLException {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            user.setUsername("updated");
            user.setDisplayName("updated");
            assertEquals(1, userTable.update(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<User> updated = userTable.get(connection, user.getId());
            assertTrue(updated.isPresent());
            assertEquals(user.getId(), updated.get().getId());
            assertEquals(user.getEmail(), updated.get().getEmail());
            assertEquals(user.getUsername(), updated.get().getUsername());
            assertEquals(user.getDisplayName(), updated.get().getDisplayName());
        }
    }

    @Test
    public void testDeleteMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userTable.delete(connection, "missing"));
        }
    }

    @Test
    public void testDelete() throws SQLException {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.delete(connection, user.getId()));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userTable.get(connection, user.getId()).isPresent());
        }
    }

    @Test
    public void testTruncate() throws SQLException {
        User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(2, userTable.truncate(connection));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<User> results = userTable.getAll(connection, new Page(), emptySet());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}
