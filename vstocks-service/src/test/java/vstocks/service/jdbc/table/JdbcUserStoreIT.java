package vstocks.service.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.Page;
import vstocks.model.Password;
import vstocks.model.Results;
import vstocks.model.User;
import vstocks.service.DataSourceExternalResource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static org.junit.Assert.*;
import static vstocks.model.UserSource.LOCAL;
import static vstocks.model.UserSource.TWITTER;

public class JdbcUserStoreIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userStore;

    @Before
    public void setup() {
        userStore = new UserTable();
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            userStore.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userStore.get(connection, "missing-id").isPresent());
        }
    }

    @Test
    public void testGetExists() throws SQLException {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<User> fetched = userStore.get(connection, user.getId());
            assertTrue(fetched.isPresent());
            assertEquals(user, fetched.get());
        }
    }

    @Test
    public void testLoginMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userStore.login(connection, "missing-username", "missing-password").isPresent());
        }
    }

    @Test
    public void testLoginUsernameExistsExactMatch() throws SQLException {
        String hashedPass = Password.hash("password");
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(LOCAL).setHashedPass(hashedPass);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<User> fetched = userStore.login(connection, user.getUsername(), hashedPass);
            assertTrue(fetched.isPresent());
            assertEquals(user, fetched.get());
        }
    }

    @Test
    public void testLoginUsernameExistsWrongCase() throws SQLException {
        String hashedPass = Password.hash("password");
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(LOCAL).setHashedPass(hashedPass);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userStore.login(connection, user.getUsername().toUpperCase(ENGLISH), "HASH").isPresent());
        }
    }

    @Test
    public void testLoginEmailExistsExactMatch() throws SQLException {
        String hashedPass = Password.hash("password");
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(LOCAL).setHashedPass(hashedPass);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<User> fetched = userStore.login(connection, user.getEmail(), hashedPass);
            assertTrue(fetched.isPresent());
            assertEquals(user, fetched.get());
        }
    }

    @Test
    public void testLoginEmailExistsWrongCase() throws SQLException {
        String hashedPass = Password.hash("password");
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(LOCAL).setHashedPass(hashedPass);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<User> fetched = userStore.login(connection, user.getEmail().toUpperCase(ENGLISH), hashedPass);
            assertTrue(fetched.isPresent());
            assertEquals(user, fetched.get());
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<User> results =  userStore.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSome() throws SQLException {
        User user1 = new User().setId("id1").setUsername("name1").setEmail("email1").setSource(TWITTER);
        User user2 = new User().setId("id2").setUsername("name2").setEmail("email2").setSource(TWITTER);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user1));
            assertEquals(1, userStore.add(connection, user2));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<User> results = userStore.getAll(connection, new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(user1));
            assertTrue(results.getResults().contains(user2));
        }
    }

    @Test
    public void testGetAllMultiplePages() throws SQLException {
        User user1 = new User().setId("id1").setUsername("name1").setEmail("email1").setSource(TWITTER);
        User user2 = new User().setId("id2").setUsername("name2").setEmail("email2").setSource(TWITTER);
        User user3 = new User().setId("id3").setUsername("name3").setEmail("email3").setSource(TWITTER);
        User user4 = new User().setId("id4").setUsername("name4").setEmail("email4").setSource(TWITTER);
        User user5 = new User().setId("id5").setUsername("name5").setEmail("email5").setSource(TWITTER);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            for (User user : asList(user1, user2, user3, user4, user5)) {
                assertEquals(1, userStore.add(connection, user));
            }
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Page page = new Page().setSize(2);
            Results<User> results = userStore.getAll(connection, page);
            assertEquals(5, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(user1));
            assertTrue(results.getResults().contains(user2));

            page = page.next();
            results = userStore.getAll(connection, page);
            assertEquals(5, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(user3));
            assertTrue(results.getResults().contains(user4));

            page = page.next();
            results = userStore.getAll(connection, page);
            assertEquals(5, results.getTotal());
            assertEquals(1, results.getResults().size());
            assertTrue(results.getResults().contains(user5));
        }
    }

    @Test
    public void testAdd() throws SQLException {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user));
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddIdConflict() throws SQLException {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user));
            user.setUsername("different").setEmail("different");
            userStore.add(connection, user);
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddUsernameConflict() throws SQLException {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user));
            user.setId("different").setEmail("different");
            userStore.add(connection, user);
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddEmailConflict() throws SQLException {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user));
            user.setId("different").setUsername("different");
            userStore.add(connection, user);
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddEmailConflictDifferentCase() throws SQLException {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user));
            user.setId("different").setUsername("different").setEmail("EMAIL");
            userStore.add(connection, user);
        }
    }

    @Test
    public void testUpdateMissing() throws SQLException {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userStore.update(connection, user));
        }
    }

    @Test
    public void testUpdate() throws SQLException {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            user.setUsername("updated");
            user.setEmail("updated");
            assertEquals(1, userStore.update(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<User> updated = userStore.get(connection, user.getId());
            assertTrue(updated.isPresent());
            assertEquals(user, updated.get());
        }
    }

    @Test
    public void testUpdatePasswordMissing() throws SQLException {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userStore.updatePassword(connection, user));
            connection.commit();
        }
    }

    @Test
    public void testUpdatePassword() throws SQLException {
        String hashedPass = Password.hash("password");
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(LOCAL).setHashedPass(hashedPass);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            user.setSource(LOCAL);
            user.setHashedPass(Password.hash("updated"));
            assertEquals(1, userStore.updatePassword(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<User> updated = userStore.get(connection, user.getId());
            assertTrue(updated.isPresent());
            assertEquals(user, updated.get());
        }
    }

    @Test
    public void testDeleteMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userStore.delete(connection, "missing"));
        }
    }

    @Test
    public void testDelete() throws SQLException {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.delete(connection, user.getId()));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userStore.get(connection, user.getId()).isPresent());
        }
    }

    @Test
    public void testTruncate() throws SQLException {
        User user1 = new User().setId("id1").setUsername("name1").setEmail("email1").setSource(TWITTER);
        User user2 = new User().setId("id2").setUsername("name2").setEmail("email2").setSource(TWITTER);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user1));
            assertEquals(1, userStore.add(connection, user2));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(2, userStore.truncate(connection));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<User> results =userStore.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}
