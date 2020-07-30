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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static vstocks.model.UserSource.TWITTER;

public class JdbcUserBalanceServiceIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private UserBalanceTable userBalanceTable;
    private JdbcUserBalanceService userBalanceService;

    private final User user1 = new User().setId("user1").setUsername("u1").setSource(TWITTER).setDisplayName("U1");
    private final User user2 = new User().setId("user2").setUsername("u2").setSource(TWITTER).setDisplayName("U2");

    @Before
    public void setup() throws SQLException {
        userTable = new UserTable();
        userBalanceTable = new UserBalanceTable();
        userBalanceService = new JdbcUserBalanceService(dataSourceExternalResource.get());

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
    public void testConsumeNone() {
        List<UserBalance> list = new ArrayList<>();
        assertEquals(0, userBalanceService.consume(list::add));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSome() {
        UserBalance userBalance1 = new UserBalance().setUserId(user1.getId()).setBalance(10);
        UserBalance userBalance2 = new UserBalance().setUserId(user2.getId()).setBalance(10);
        assertEquals(1, userBalanceService.add(userBalance1));
        assertEquals(1, userBalanceService.add(userBalance2));

        List<UserBalance> list = new ArrayList<>();
        assertEquals(2, userBalanceService.consume(list::add));
        assertEquals(2, list.size());
        assertTrue(list.contains(userBalance1));
        assertTrue(list.contains(userBalance2));
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
