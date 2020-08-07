package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.*;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.jdbc.table.UserBalanceTable;
import vstocks.db.jdbc.table.UserTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Locale.ENGLISH;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Sort.SortDirection.DESC;

public class JdbcUserDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private UserBalanceTable userBalanceTable;

    private JdbcUserDB userService;
    private JdbcUserBalanceDB userBalanceService;

    @Before
    public void setup() {
        userTable = new UserTable();
        userBalanceTable = new UserBalanceTable();
        userService = new JdbcUserDB(dataSourceExternalResource.get());
        userBalanceService = new JdbcUserBalanceDB(dataSourceExternalResource.get());
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
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        assertEquals(1, userService.add(user));
        assertTrue(userService.usernameExists(user.getUsername()));
    }

    @Test
    public void testGetMissing() {
        assertFalse(userService.get("missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        assertEquals(1, userService.add(user));

        Optional<User> fetched = userService.get(user.getId());
        assertTrue(fetched.isPresent());
        assertEquals(user.getId(), fetched.get().getId());
        assertEquals(user.getEmail(), fetched.get().getEmail());
        assertEquals(user.getUsername(), fetched.get().getUsername());
        assertEquals(user.getDisplayName(), fetched.get().getDisplayName());
    }

    @Test
    public void testGetLowercaseEmail() {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        assertEquals(1, userService.add(user));

        Optional<User> fetched = userService.get(user.getId());
        assertTrue(fetched.isPresent());
        assertEquals(user.getId(), fetched.get().getId());
        assertEquals(user.getEmail().toLowerCase(ENGLISH), fetched.get().getEmail());
        assertEquals(user.getUsername(), fetched.get().getUsername());
        assertEquals(user.getDisplayName(), fetched.get().getDisplayName());
    }

    @Test
    public void testGetAllNone() {
        Results<User> results =  userService.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
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
        User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
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
        User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
        User user3 = new User().setEmail("user3@domain.com").setUsername("name3").setDisplayName("Name3");
        User user4 = new User().setEmail("user4@domain.com").setUsername("name4").setDisplayName("Name4");
        User user5 = new User().setEmail("user5@domain.com").setUsername("name5").setDisplayName("Name5");
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
        User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
        User user3 = new User().setEmail("user3@domain.com").setUsername("name3").setDisplayName("Name3");
        User user4 = new User().setEmail("user4@domain.com").setUsername("name4").setDisplayName("Name4");
        User user5 = new User().setEmail("user5@domain.com").setUsername("name5").setDisplayName("Name5");
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
        User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
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
        User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
        User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
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
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        assertEquals(1, userService.add(user));

        Optional<UserBalance> userBalance = userBalanceService.get(user.getId());
        assertTrue(userBalance.isPresent());
        assertEquals(10000, userBalance.get().getBalance());
    }

    @Test(expected = Exception.class)
    public void testAddUsernameConflict() {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        assertEquals(1, userService.add(user));
        user.setEmail("different");
        userService.add(user);
    }

    @Test(expected = Exception.class)
    public void testAddEmailConflict() {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        assertEquals(1, userService.add(user));
        user.setUsername("different");
        userService.add(user);
    }

    @Test(expected = Exception.class)
    public void testAddEmailConflictDifferentCase() {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        assertEquals(1, userService.add(user));
        user.setEmail("USER@DOMAIN.COM");
        userService.add(user);
    }

    @Test
    public void testUpdateMissing() {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        assertEquals(0, userService.update(user));
    }

    @Test
    public void testUpdate() {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        assertEquals(1, userService.add(user));

        user.setUsername("updated");
        user.setDisplayName("updated");
        assertEquals(1, userService.update(user));

        Optional<User> updated = userService.get(user.getId());
        assertTrue(updated.isPresent());
        assertEquals(user.getId(), updated.get().getId());
        assertEquals(user.getEmail(), updated.get().getEmail());
        assertEquals(user.getUsername(), updated.get().getUsername());
        assertEquals(user.getDisplayName(), updated.get().getDisplayName());
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, userService.delete("missing"));
    }

    @Test
    public void testDelete() {
        User user = new User().setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        assertEquals(1, userService.add(user));
        assertEquals(1, userService.delete(user.getId()));
        assertFalse(userService.get(user.getId()).isPresent());
    }
}
