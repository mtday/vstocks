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
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Sort.SortDirection.DESC;

public class JdbcUserBalanceDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private UserBalanceTable userBalanceTable;
    private JdbcUserBalanceDB userBalanceService;

    private final User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
    private final User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");

    @Before
    public void setup() throws SQLException {
        userTable = new UserTable();
        userBalanceTable = new UserBalanceTable();
        userBalanceService = new JdbcUserBalanceDB(dataSourceExternalResource.get());

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            connection.commit();
        }
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
    public void testGetMissing() {
        assertFalse(userBalanceService.get("missing-user").isPresent());
    }

    @Test
    public void testGetExists() {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        assertEquals(1, userBalanceService.add(userBalance));

        Optional<UserBalance> fetched = userBalanceService.get(userBalance.getUserId());
        assertTrue(fetched.isPresent());
        assertEquals(userBalance.getBalance(), fetched.get().getBalance());
    }

    @Test
    public void testGetAllNone() {
        Results<UserBalance> results = userBalanceService.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        UserBalance userBalance1 = new UserBalance().setUserId(user1.getId()).setBalance(10);
        UserBalance userBalance2 = new UserBalance().setUserId(user2.getId()).setBalance(10);
        assertEquals(1, userBalanceService.add(userBalance1));
        assertEquals(1, userBalanceService.add(userBalance2));

        Results<UserBalance> results = userBalanceService.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userBalance1, results.getResults().get(0));
        assertEquals(userBalance2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        UserBalance userBalance1 = new UserBalance().setUserId(user1.getId()).setBalance(10);
        UserBalance userBalance2 = new UserBalance().setUserId(user2.getId()).setBalance(10);
        assertEquals(1, userBalanceService.add(userBalance1));
        assertEquals(1, userBalanceService.add(userBalance2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), BALANCE.toSort()));
        Results<UserBalance> results = userBalanceService.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userBalance2, results.getResults().get(0));
        assertEquals(userBalance1, results.getResults().get(1));
    }

    @Test
    public void testConsumeNone() {
        List<UserBalance> list = new ArrayList<>();
        assertEquals(0, userBalanceService.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        UserBalance userBalance1 = new UserBalance().setUserId(user1.getId()).setBalance(10);
        UserBalance userBalance2 = new UserBalance().setUserId(user2.getId()).setBalance(10);
        assertEquals(1, userBalanceService.add(userBalance1));
        assertEquals(1, userBalanceService.add(userBalance2));

        List<UserBalance> list = new ArrayList<>();
        assertEquals(2, userBalanceService.consume(list::add, emptySet()));
        assertEquals(2, list.size());
        assertEquals(userBalance1, list.get(0));
        assertEquals(userBalance2, list.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        UserBalance userBalance1 = new UserBalance().setUserId(user1.getId()).setBalance(10);
        UserBalance userBalance2 = new UserBalance().setUserId(user2.getId()).setBalance(10);
        assertEquals(1, userBalanceService.add(userBalance1));
        assertEquals(1, userBalanceService.add(userBalance2));

        List<UserBalance> list = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), BALANCE.toSort()));
        assertEquals(2, userBalanceService.consume(list::add, sort));
        assertEquals(2, list.size());
        assertEquals(userBalance2, list.get(0));
        assertEquals(userBalance1, list.get(1));
    }

    @Test
    public void testSetInitialBalanceNoneExists() {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        assertEquals(1, userBalanceService.setInitialBalance(userBalance));

        Optional<UserBalance> fetched = userBalanceService.get(userBalance.getUserId());
        assertTrue(fetched.isPresent());
        assertEquals(10, fetched.get().getBalance());
    }

    @Test
    public void testSetInitialBalanceAlreadyExists() {
        UserBalance existingBalance = new UserBalance().setUserId(user1.getId()).setBalance(20);
        assertEquals(1, userBalanceService.setInitialBalance(existingBalance));

        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        assertEquals(0, userBalanceService.setInitialBalance(userBalance));

        Optional<UserBalance> fetched = userBalanceService.get(userBalance.getUserId());
        assertTrue(fetched.isPresent());
        assertEquals(20, fetched.get().getBalance());
    }

    @Test
    public void testAdd() {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        assertEquals(1, userBalanceService.add(userBalance));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        assertEquals(1, userBalanceService.add(userBalance));
        userBalanceService.add(userBalance);
    }

    @Test
    public void testUpdateIncrementMissing() {
        assertEquals(0, userBalanceService.update("missing-id", 10));
    }

    @Test
    public void testUpdateIncrement() {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        assertEquals(1, userBalanceService.add(userBalance));
        assertEquals(1, userBalanceService.update(userBalance.getUserId(), 10));

        Optional<UserBalance> updated = userBalanceService.get(userBalance.getUserId());
        assertTrue(updated.isPresent());
        assertEquals(20, updated.get().getBalance());
    }

    @Test
    public void testUpdateDecrementMissing() {
        assertEquals(0, userBalanceService.update("missing-id", -10));
    }

    @Test
    public void testUpdateDecrementTooFar() {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        assertEquals(1, userBalanceService.add(userBalance));
        assertEquals(0, userBalanceService.update(userBalance.getUserId(), -12));

        Optional<UserBalance> fetched = userBalanceService.get(userBalance.getUserId());
        assertTrue(fetched.isPresent());
        assertEquals(userBalance.getBalance(), fetched.get().getBalance()); // not updated
    }

    @Test
    public void testUpdateDecrement() {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        assertEquals(1, userBalanceService.add(userBalance));
        assertEquals(1, userBalanceService.update(userBalance.getUserId(), -8));

        Optional<UserBalance> fetched = userBalanceService.get(userBalance.getUserId());
        assertTrue(fetched.isPresent());
        assertEquals(2, fetched.get().getBalance());
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, userBalanceService.delete("missing"));
    }

    @Test
    public void testDelete() {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        assertEquals(1, userBalanceService.add(userBalance));
        assertEquals(1, userBalanceService.delete(userBalance.getUserId()));
        assertFalse(userBalanceService.get(userBalance.getUserId()).isPresent());
    }
}
