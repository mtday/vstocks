package vstocks.service.db.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.User;
import vstocks.service.db.DataSourceExternalResource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static vstocks.model.UserSource.TwitterClient;

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
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
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
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<User> fetched = userTable.get(connection, user.getId());
            assertTrue(fetched.isPresent());
            assertEquals(user.getUsername(), fetched.get().getUsername());
            assertEquals(user.getSource(), fetched.get().getSource());
            assertEquals(user.getDisplayName(), fetched.get().getDisplayName());
        }
    }

    @Test
    public void testLoginMissing() throws SQLException {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.login(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<User> fetched = userTable.get(connection, user.getId());
            assertTrue(fetched.isPresent());
            assertEquals(user.getUsername(), fetched.get().getUsername());
            assertEquals(user.getSource(), fetched.get().getSource());
            assertEquals(user.getDisplayName(), fetched.get().getDisplayName());
        }
    }

    @Test
    public void testLoginExistsSame() throws SQLException {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userTable.login(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<User> fetched = userTable.get(connection, user.getId());
            assertTrue(fetched.isPresent());
            assertEquals(user.getUsername(), fetched.get().getUsername());
            assertEquals(user.getSource(), fetched.get().getSource());
            assertEquals(user.getDisplayName(), fetched.get().getDisplayName());
        }
    }

    @Test
    public void testLoginExistsDifferent() throws SQLException {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user));
            connection.commit();
        }
        User loginUser = new User().setId("id").setUsername("login").setSource(TwitterClient).setDisplayName("Login");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.login(connection, loginUser));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<User> fetched = userTable.get(connection, user.getId());
            assertTrue(fetched.isPresent());
            assertEquals(loginUser, fetched.get());
            assertEquals(loginUser.getUsername(), fetched.get().getUsername());
            assertEquals(loginUser.getSource(), fetched.get().getSource());
            assertEquals(loginUser.getDisplayName(), fetched.get().getDisplayName());
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<User> results =  userTable.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSome() throws SQLException {
        User user1 = new User().setId("id1").setUsername("name1").setSource(TwitterClient).setDisplayName("Name");
        User user2 = new User().setId("id2").setUsername("name2").setSource(TwitterClient).setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<User> results = userTable.getAll(connection, new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(user1));
            assertTrue(results.getResults().contains(user2));
        }
    }

    @Test
    public void testGetAllMultiplePages() throws SQLException {
        User user1 = new User().setId("id1").setUsername("name1").setSource(TwitterClient).setDisplayName("Name");
        User user2 = new User().setId("id2").setUsername("name2").setSource(TwitterClient).setDisplayName("Name");
        User user3 = new User().setId("id3").setUsername("name3").setSource(TwitterClient).setDisplayName("Name");
        User user4 = new User().setId("id4").setUsername("name4").setSource(TwitterClient).setDisplayName("Name");
        User user5 = new User().setId("id5").setUsername("name5").setSource(TwitterClient).setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            for (User user : asList(user1, user2, user3, user4, user5)) {
                assertEquals(1, userTable.add(connection, user));
            }
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Page page = new Page().setSize(2);
            Results<User> results = userTable.getAll(connection, page);
            assertEquals(5, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(user1));
            assertTrue(results.getResults().contains(user2));

            page = page.next();
            results = userTable.getAll(connection, page);
            assertEquals(5, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(user3));
            assertTrue(results.getResults().contains(user4));

            page = page.next();
            results = userTable.getAll(connection, page);
            assertEquals(5, results.getTotal());
            assertEquals(1, results.getResults().size());
            assertTrue(results.getResults().contains(user5));
        }
    }

    @Test
    public void testConsumeNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<User> list = new ArrayList<>();
            assertEquals(0, userTable.consume(connection, list::add));
            assertTrue(list.isEmpty());
        }
    }

    @Test
    public void testConsumeSome() throws SQLException {
        User user1 = new User().setId("id1").setUsername("name1").setSource(TwitterClient).setDisplayName("Name");
        User user2 = new User().setId("id2").setUsername("name2").setSource(TwitterClient).setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<User> list = new ArrayList<>();
            assertEquals(2, userTable.consume(connection, list::add));
            assertEquals(2, list.size());
            assertTrue(list.contains(user1));
            assertTrue(list.contains(user2));
        }
    }

    @Test
    public void testAdd() throws SQLException {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user));
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddIdConflict() throws SQLException {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user));
            user.setUsername("different");
            userTable.add(connection, user);
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddUsernameConflict() throws SQLException {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user));
            user.setId("different");
            userTable.add(connection, user);
            connection.commit();
        }
    }

    @Test
    public void testUpdateMissing() throws SQLException {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userTable.update(connection, user));
        }
    }

    @Test
    public void testUpdateNoChange() throws SQLException {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
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
            assertEquals(user.getUsername(), updated.get().getUsername());
            assertEquals(user.getSource(), updated.get().getSource());
            assertEquals(user.getDisplayName(), updated.get().getDisplayName());
        }
    }

    @Test
    public void testUpdate() throws SQLException {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
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
            assertEquals(user.getUsername(), updated.get().getUsername());
            assertEquals(user.getSource(), updated.get().getSource());
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
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
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
        User user1 = new User().setId("id1").setUsername("name1").setSource(TwitterClient).setDisplayName("Name");
        User user2 = new User().setId("id2").setUsername("name2").setSource(TwitterClient).setDisplayName("Name");
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
            Results<User> results = userTable.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}