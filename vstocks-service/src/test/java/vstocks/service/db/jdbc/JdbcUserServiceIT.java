package vstocks.service.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.*;
import vstocks.service.db.DataSourceExternalResource;
import vstocks.service.db.jdbc.table.UserBalanceTable;
import vstocks.service.db.jdbc.table.UserTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Sort.SortDirection.DESC;
import static vstocks.model.UserSource.TwitterClient;

public class JdbcUserServiceIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private UserBalanceTable userBalanceTable;

    private JdbcUserService userService;
    private JdbcUserBalanceService userBalanceService;

    @Before
    public void setup() {
        userTable = new UserTable();
        userBalanceTable = new UserBalanceTable();
        userService = new JdbcUserService(dataSourceExternalResource.get());
        userBalanceService = new JdbcUserBalanceService(dataSourceExternalResource.get());
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            userBalanceTable.truncate(connection);
            userTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testUsernameExistsMissing() {
        assertFalse(userService.usernameExists("missing"));
    }

    @Test
    public void testUsernameExists() {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        assertEquals(1, userService.add(user));
        assertTrue(userService.usernameExists(user.getUsername()));
    }

    @Test
    public void testGetMissing() {
        assertFalse(userService.get("missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        assertEquals(1, userService.add(user));

        Optional<User> fetched = userService.get(user.getId());
        assertTrue(fetched.isPresent());
        assertEquals(user.getUsername(), fetched.get().getUsername());
        assertEquals(user.getSource(), fetched.get().getSource());
        assertEquals(user.getDisplayName(), fetched.get().getDisplayName());
    }

    @Test
    public void testLoginMissing() {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        assertEquals(1, userService.login(user));

        Optional<User> fetched = userService.get(user.getId());
        assertTrue(fetched.isPresent());
        assertEquals(user.getUsername(), fetched.get().getUsername());
        assertEquals(user.getSource(), fetched.get().getSource());
        assertEquals(user.getDisplayName(), fetched.get().getDisplayName());

        Optional<UserBalance> userBalance = userBalanceService.get(user.getId());
        assertTrue(userBalance.isPresent());
        assertEquals(10000, userBalance.get().getBalance());
    }

    @Test
    public void testLoginExistsSame() {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        assertEquals(1, userService.add(user));

        Optional<User> fetched = userService.get(user.getId());
        assertTrue(fetched.isPresent());
        assertEquals(user.getUsername(), fetched.get().getUsername());
        assertEquals(user.getSource(), fetched.get().getSource());
        assertEquals(user.getDisplayName(), fetched.get().getDisplayName());

        assertEquals(0, userService.login(user));

        fetched = userService.get(user.getId());
        assertTrue(fetched.isPresent());
        assertEquals(user.getUsername(), fetched.get().getUsername());
        assertEquals(user.getSource(), fetched.get().getSource());
        assertEquals(user.getDisplayName(), fetched.get().getDisplayName());

        assertFalse(userBalanceService.get(user.getId()).isPresent());
    }

    @Test
    public void testLoginExistsDifferent() {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        assertEquals(1, userService.add(user));

        User loginUser = new User().setId("id").setUsername("login").setSource(TwitterClient).setDisplayName("Login");
        assertEquals(1, userService.login(loginUser));

        Optional<User> fetched = userService.get(user.getId());
        assertTrue(fetched.isPresent());
        assertEquals(loginUser.getUsername(), fetched.get().getUsername());
        assertEquals(loginUser.getSource(), fetched.get().getSource());
        assertEquals(loginUser.getDisplayName(), fetched.get().getDisplayName());

        Optional<UserBalance> userBalance = userBalanceService.get(user.getId());
        assertTrue(userBalance.isPresent());
        assertEquals(10000, userBalance.get().getBalance());
    }

    @Test
    public void testGetAllNone() {
        Results<User> results =  userService.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        User user1 = new User().setId("id1").setUsername("name1").setSource(TwitterClient).setDisplayName("Name");
        User user2 = new User().setId("id2").setUsername("name2").setSource(TwitterClient).setDisplayName("Name");
        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));

        Results<User> results = userService.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(user1, results.getResults().get(0));
        assertEquals(user2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        User user1 = new User().setId("id1").setUsername("name1").setSource(TwitterClient).setDisplayName("Name");
        User user2 = new User().setId("id2").setUsername("name2").setSource(TwitterClient).setDisplayName("Name");
        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USERNAME.toSort(DESC), DISPLAY_NAME.toSort()));
        Results<User> results = userService.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(user2, results.getResults().get(0));
        assertEquals(user1, results.getResults().get(1));
    }

    @Test
    public void testGetAllMultiplePagesNoSort() {
        User user1 = new User().setId("id1").setUsername("name1").setSource(TwitterClient).setDisplayName("Name");
        User user2 = new User().setId("id2").setUsername("name2").setSource(TwitterClient).setDisplayName("Name");
        User user3 = new User().setId("id3").setUsername("name3").setSource(TwitterClient).setDisplayName("Name");
        User user4 = new User().setId("id4").setUsername("name4").setSource(TwitterClient).setDisplayName("Name");
        User user5 = new User().setId("id5").setUsername("name5").setSource(TwitterClient).setDisplayName("Name");
        for (User user : asList(user1, user2, user3, user4, user5)) {
            assertEquals(1, userService.add(user));
        }

        Page page = new Page().setSize(2);
        Results<User> results = userService.getAll(page, emptySet());
        assertEquals(5, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(user1, results.getResults().get(0));
        assertEquals(user2, results.getResults().get(1));

        page = page.next();
        results = userService.getAll(page, emptySet());
        assertEquals(5, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(user3, results.getResults().get(0));
        assertEquals(user4, results.getResults().get(1));

        page = page.next();
        results = userService.getAll(page, emptySet());
        assertEquals(5, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(user5, results.getResults().get(0));
    }

    @Test
    public void testGetAllMultiplePagesWithSort() {
        User user1 = new User().setId("id1").setUsername("name1").setSource(TwitterClient).setDisplayName("Name");
        User user2 = new User().setId("id2").setUsername("name2").setSource(TwitterClient).setDisplayName("Name");
        User user3 = new User().setId("id3").setUsername("name3").setSource(TwitterClient).setDisplayName("Name");
        User user4 = new User().setId("id4").setUsername("name4").setSource(TwitterClient).setDisplayName("Name");
        User user5 = new User().setId("id5").setUsername("name5").setSource(TwitterClient).setDisplayName("Name");
        for (User user : asList(user1, user2, user3, user4, user5)) {
            assertEquals(1, userService.add(user));
        }

        Page page = new Page().setSize(2);
        Set<Sort> sort = new LinkedHashSet<>(asList(USERNAME.toSort(DESC), DISPLAY_NAME.toSort()));
        Results<User> results = userService.getAll(page, sort);
        assertEquals(5, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(user5, results.getResults().get(0));
        assertEquals(user4, results.getResults().get(1));

        page = page.next();
        results = userService.getAll(page, sort);
        assertEquals(5, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(user3, results.getResults().get(0));
        assertEquals(user2, results.getResults().get(1));

        page = page.next();
        results = userService.getAll(page, sort);
        assertEquals(5, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(user1, results.getResults().get(0));
    }

    @Test
    public void testConsumeNone() {
        List<User> list = new ArrayList<>();
        assertEquals(0, userService.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        User user1 = new User().setId("id1").setUsername("name1").setSource(TwitterClient).setDisplayName("Name");
        User user2 = new User().setId("id2").setUsername("name2").setSource(TwitterClient).setDisplayName("Name");
        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));

        List<User> list = new ArrayList<>();
        assertEquals(2, userService.consume(list::add, emptySet()));
        assertEquals(2, list.size());
        assertEquals(user1, list.get(0));
        assertEquals(user2, list.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        User user1 = new User().setId("id1").setUsername("name1").setSource(TwitterClient).setDisplayName("Name");
        User user2 = new User().setId("id2").setUsername("name2").setSource(TwitterClient).setDisplayName("Name");
        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));

        List<User> list = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(USERNAME.toSort(DESC), DISPLAY_NAME.toSort()));
        assertEquals(2, userService.consume(list::add, sort));
        assertEquals(2, list.size());
        assertEquals(user2, list.get(0));
        assertEquals(user1, list.get(1));
    }

    @Test
    public void testAdd() {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        assertEquals(1, userService.add(user));
    }

    @Test(expected = Exception.class)
    public void testAddIdConflict() {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        assertEquals(1, userService.add(user));
        user.setUsername("different");
        userService.add(user);
    }

    @Test(expected = Exception.class)
    public void testAddUsernameConflict() {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        assertEquals(1, userService.add(user));
        user.setId("different");
        userService.add(user);
    }

    @Test
    public void testUpdateMissing() {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        assertEquals(0, userService.update(user));
    }

    @Test
    public void testUpdate() {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        assertEquals(1, userService.add(user));

        user.setUsername("updated");
        user.setDisplayName("updated");
        assertEquals(1, userService.update(user));

        Optional<User> updated = userService.get(user.getId());
        assertTrue(updated.isPresent());
        assertEquals(user.getUsername(), updated.get().getUsername());
        assertEquals(user.getSource(), updated.get().getSource());
        assertEquals(user.getDisplayName(), updated.get().getDisplayName());
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, userService.delete("missing"));
    }

    @Test
    public void testDelete() {
        User user = new User().setId("id").setUsername("name").setSource(TwitterClient).setDisplayName("Name");
        assertEquals(1, userService.add(user));
        assertEquals(1, userService.delete(user.getId()));
        assertFalse(userService.get(user.getId()).isPresent());
    }
}
