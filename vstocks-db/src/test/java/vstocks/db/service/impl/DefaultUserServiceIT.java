package vstocks.db.service.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.store.impl.JdbcUserStore;
import vstocks.model.Page;
import vstocks.model.Password;
import vstocks.model.Results;
import vstocks.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static org.junit.Assert.*;
import static vstocks.model.UserSource.LOCAL;
import static vstocks.model.UserSource.TWITTER;

public class DefaultUserServiceIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private JdbcUserStore userStore;
    private DefaultUserService userService;

    @Before
    public void setup() {
        userStore = new JdbcUserStore();
        userService = new DefaultUserService(dataSourceExternalResource.get(), userStore);
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            userStore.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() {
        assertFalse(userService.get("missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        assertEquals(1, userService.add(user));

        Optional<User> fetched = userService.get(user.getId());
        assertTrue(fetched.isPresent());
        assertEquals(user, fetched.get());
    }

    @Test
    public void testLoginMissing() {
        assertFalse(userService.login("missing-username", "missing-password").isPresent());
    }

    @Test
    public void testLoginUsernameExistsExactMatch() {
        String hashedPass = Password.hash("password");
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(LOCAL).setHashedPass(hashedPass);
        assertEquals(1, userService.add(user));

        Optional<User> fetched = userService.login(user.getUsername(), hashedPass);
        assertTrue(fetched.isPresent());
        assertEquals(user, fetched.get());
    }

    @Test
    public void testLoginUsernameExistsWrongCase() {
        String hashedPass = Password.hash("password");
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(LOCAL).setHashedPass(hashedPass);
        assertEquals(1, userService.add(user));
        assertFalse(userService.login(user.getUsername().toUpperCase(ENGLISH), "HASH").isPresent());
    }

    @Test
    public void testLoginEmailExistsExactMatch() {
        String hashedPass = Password.hash("password");
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(LOCAL).setHashedPass(hashedPass);
        assertEquals(1, userService.add(user));

        Optional<User> fetched = userService.login(user.getEmail(), hashedPass);
        assertTrue(fetched.isPresent());
        assertEquals(user, fetched.get());
    }

    @Test
    public void testLoginEmailExistsWrongCase() {
        String hashedPass = Password.hash("password");
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(LOCAL).setHashedPass(hashedPass);
        assertEquals(1, userService.add(user));

        Optional<User> fetched = userService.login(user.getEmail().toUpperCase(ENGLISH), hashedPass);
        assertTrue(fetched.isPresent());
        assertEquals(user, fetched.get());
    }

    @Test
    public void testGetAllNone() {
        Results<User> results =  userService.getAll(new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSome() {
        User user1 = new User().setId("id1").setUsername("name1").setEmail("email1").setSource(TWITTER);
        User user2 = new User().setId("id2").setUsername("name2").setEmail("email2").setSource(TWITTER);
        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));

        Results<User> results = userService.getAll(new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(user1));
        assertTrue(results.getResults().contains(user2));
    }

    @Test
    public void testGetAllMultiplePages() {
        User user1 = new User().setId("id1").setUsername("name1").setEmail("email1").setSource(TWITTER);
        User user2 = new User().setId("id2").setUsername("name2").setEmail("email2").setSource(TWITTER);
        User user3 = new User().setId("id3").setUsername("name3").setEmail("email3").setSource(TWITTER);
        User user4 = new User().setId("id4").setUsername("name4").setEmail("email4").setSource(TWITTER);
        User user5 = new User().setId("id5").setUsername("name5").setEmail("email5").setSource(TWITTER);
        for (User user : asList(user1, user2, user3, user4, user5)) {
            assertEquals(1, userService.add(user));
        }

        Page page = new Page().setSize(2);
        Results<User> results = userService.getAll(page);
        assertEquals(5, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(user1));
        assertTrue(results.getResults().contains(user2));

        page = page.next();
        results = userService.getAll(page);
        assertEquals(5, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(user3));
        assertTrue(results.getResults().contains(user4));

        page = page.next();
        results = userService.getAll(page);
        assertEquals(5, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertTrue(results.getResults().contains(user5));
    }

    @Test
    public void testAdd() {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        assertEquals(1, userService.add(user));
    }

    @Test(expected = Exception.class)
    public void testAddIdConflict() {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        assertEquals(1, userService.add(user));
        user.setUsername("different").setEmail("different");
        userService.add(user);
    }

    @Test(expected = Exception.class)
    public void testAddUsernameConflict() {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        assertEquals(1, userService.add(user));
        user.setId("different").setEmail("different");
        userService.add(user);
    }

    @Test(expected = Exception.class)
    public void testAddEmailConflict() {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        assertEquals(1, userService.add(user));
        user.setId("different").setUsername("different");
        userService.add(user);
    }

    @Test(expected = Exception.class)
    public void testAddEmailConflictDifferentCase() {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        assertEquals(1, userService.add(user));
        user.setId("different").setUsername("different").setEmail("EMAIL");
        userService.add(user);
    }

    @Test
    public void testUpdateMissing() {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        assertEquals(0, userService.update(user));
    }

    @Test
    public void testUpdate() {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        assertEquals(1, userService.add(user));

        user.setUsername("updated");
        user.setEmail("updated");
        assertEquals(1, userService.update(user));

        Optional<User> updated = userService.get(user.getId());
        assertTrue(updated.isPresent());
        assertEquals(user, updated.get());
    }

    @Test
    public void testUpdatePasswordMissing() {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        assertEquals(0, userService.updatePassword(user));
    }

    @Test
    public void testUpdatePassword() {
        String hashedPass = Password.hash("password");
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(LOCAL).setHashedPass(hashedPass);
        assertEquals(1, userService.add(user));

        user.setSource(LOCAL);
        user.setHashedPass(Password.hash("updated"));
        assertEquals(1, userService.updatePassword(user));

        Optional<User> updated = userService.get(user.getId());
        assertTrue(updated.isPresent());
        assertEquals(user, updated.get());
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, userService.delete("missing"));
    }

    @Test
    public void testDelete() {
        User user = new User().setId("id").setUsername("name").setEmail("email").setSource(TWITTER);
        assertEquals(1, userService.add(user));
        assertEquals(1, userService.delete(user.getId()));
        assertFalse(userService.get(user.getId()).isPresent());
    }
}
