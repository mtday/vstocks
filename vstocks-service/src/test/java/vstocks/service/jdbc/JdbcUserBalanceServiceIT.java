package vstocks.service.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.service.DataSourceExternalResource;
import vstocks.service.jdbc.table.UserBalanceTable;
import vstocks.service.jdbc.table.UserTable;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.User;
import vstocks.model.UserBalance;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.*;
import static vstocks.model.UserSource.TWITTER;

public class JdbcUserBalanceServiceIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userStore;
    private UserBalanceTable userBalanceStore;
    private JdbcUserBalanceService userBalanceService;

    private final User user1 = new User().setId("user1").setUsername("u1").setEmail("email1").setSource(TWITTER);
    private final User user2 = new User().setId("user2").setUsername("u2").setEmail("email2").setSource(TWITTER);

    @Before
    public void setup() throws SQLException {
        userStore = new UserTable();
        userBalanceStore = new UserBalanceTable();
        userBalanceService = new JdbcUserBalanceService(dataSourceExternalResource.get());

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user1));
            assertEquals(1, userStore.add(connection, user2));
            connection.commit();
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            userBalanceStore.truncate(connection);
            userStore.truncate(connection);
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
        assertEquals(userBalance, fetched.get());
    }

    @Test
    public void testGetAllNone() {
        Results<UserBalance> results = userBalanceService.getAll(new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSome() {
        UserBalance userBalance1 = new UserBalance().setUserId(user1.getId()).setBalance(10);
        UserBalance userBalance2 = new UserBalance().setUserId(user2.getId()).setBalance(10);
        assertEquals(1, userBalanceService.add(userBalance1));
        assertEquals(1, userBalanceService.add(userBalance2));

        Results<UserBalance> results = userBalanceService.getAll(new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(userBalance1));
        assertTrue(results.getResults().contains(userBalance2));
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
        assertEquals(userBalance, fetched.get()); // not updated
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
