package vstocks.service.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.service.DataSourceExternalResource;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.User;
import vstocks.model.UserBalance;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.*;
import static vstocks.model.UserSource.TWITTER;

public class JdbcUserBalanceStoreIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userStore;
    private UserBalanceTable userBalanceStore;

    private final User user1 = new User().setId("user1").setUsername("u1").setEmail("email1").setSource(TWITTER);
    private final User user2 = new User().setId("user2").setUsername("u2").setEmail("email2").setSource(TWITTER);

    @Before
    public void setup() throws SQLException {
        userStore = new UserTable();
        userBalanceStore = new UserBalanceTable();

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
    public void testGetMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userBalanceStore.get(connection, "missing-user").isPresent());
        }
    }

    @Test
    public void testGetExists() throws SQLException {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceStore.add(connection, userBalance));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserBalance> fetched = userBalanceStore.get(connection, userBalance.getUserId());
            assertTrue(fetched.isPresent());
            assertEquals(userBalance, fetched.get());
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserBalance> results = userBalanceStore.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSome() throws SQLException {
        UserBalance userBalance1 = new UserBalance().setUserId(user1.getId()).setBalance(10);
        UserBalance userBalance2 = new UserBalance().setUserId(user2.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceStore.add(connection, userBalance1));
            assertEquals(1, userBalanceStore.add(connection, userBalance2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserBalance> results = userBalanceStore.getAll(connection, new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(userBalance1));
            assertTrue(results.getResults().contains(userBalance2));
        }
    }

    @Test
    public void testAdd() throws SQLException {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceStore.add(connection, userBalance));
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddConflict() throws SQLException {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceStore.add(connection, userBalance));
            userBalanceStore.add(connection, userBalance);
            connection.commit();
        }
    }

    @Test
    public void testUpdateIncrementMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userBalanceStore.update(connection, "missing-id", 10));
            connection.commit();
        }
    }

    @Test
    public void testUpdateIncrement() throws SQLException {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceStore.add(connection, userBalance));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceStore.update(connection, userBalance.getUserId(), 10));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserBalance> updated = userBalanceStore.get(connection, userBalance.getUserId());
            assertTrue(updated.isPresent());
            assertEquals(20, updated.get().getBalance());
        }
    }

    @Test
    public void testUpdateDecrementMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userBalanceStore.update(connection, "missing-id", -10));
        }
    }

    @Test
    public void testUpdateDecrementTooFar() throws SQLException {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceStore.add(connection, userBalance));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userBalanceStore.update(connection, userBalance.getUserId(), -12));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserBalance> fetched = userBalanceStore.get(connection, userBalance.getUserId());
            assertTrue(fetched.isPresent());
            assertEquals(userBalance, fetched.get()); // not updated
        }
    }

    @Test
    public void testUpdateDecrement() throws SQLException {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceStore.add(connection, userBalance));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceStore.update(connection, userBalance.getUserId(), -8));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserBalance> fetched = userBalanceStore.get(connection, userBalance.getUserId());
            assertTrue(fetched.isPresent());
            assertEquals(2, fetched.get().getBalance());
        }
    }

    @Test
    public void testDeleteMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userBalanceStore.delete(connection, "missing"));
            connection.commit();
        }
    }

    @Test
    public void testDelete() throws SQLException {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceStore.add(connection, userBalance));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceStore.delete(connection, userBalance.getUserId()));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userBalanceStore.get(connection, userBalance.getUserId()).isPresent());
        }
    }

    @Test
    public void testTruncate() throws SQLException {
        UserBalance userBalance1 = new UserBalance().setUserId(user1.getId()).setBalance(10);
        UserBalance userBalance2 = new UserBalance().setUserId(user2.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceStore.add(connection, userBalance1));
            assertEquals(1, userBalanceStore.add(connection, userBalance2));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(2, userBalanceStore.truncate(connection));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserBalance> results = userBalanceStore.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}
