package vstocks.service.db.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.*;
import vstocks.service.db.DataSourceExternalResource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Sort.SortDirection.DESC;
import static vstocks.model.UserSource.TwitterClient;

public class JdbcUserBalanceTableIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private UserBalanceTable userBalanceTable;

    private final User user1 = new User().setId("user1").setUsername("u1").setSource(TwitterClient).setDisplayName("U1");
    private final User user2 = new User().setId("user2").setUsername("u2").setSource(TwitterClient).setDisplayName("U2");

    @Before
    public void setup() throws SQLException {
        userTable = new UserTable();
        userBalanceTable = new UserBalanceTable();

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
    public void testGetMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userBalanceTable.get(connection, "missing-user").isPresent());
        }
    }

    @Test
    public void testGetExists() throws SQLException {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.add(connection, userBalance));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserBalance> fetched = userBalanceTable.get(connection, userBalance.getUserId());
            assertTrue(fetched.isPresent());
            assertEquals(userBalance.getBalance(), fetched.get().getBalance());
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserBalance> results = userBalanceTable.getAll(connection, new Page(), emptySet());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSomeNoSort() throws SQLException {
        UserBalance userBalance1 = new UserBalance().setUserId(user1.getId()).setBalance(10);
        UserBalance userBalance2 = new UserBalance().setUserId(user2.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.add(connection, userBalance1));
            assertEquals(1, userBalanceTable.add(connection, userBalance2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserBalance> results = userBalanceTable.getAll(connection, new Page(), emptySet());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(userBalance1, results.getResults().get(0));
            assertEquals(userBalance2, results.getResults().get(1));
        }
    }

    @Test
    public void testGetAllSomeWithSort() throws SQLException {
        UserBalance userBalance1 = new UserBalance().setUserId(user1.getId()).setBalance(10);
        UserBalance userBalance2 = new UserBalance().setUserId(user2.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.add(connection, userBalance1));
            assertEquals(1, userBalanceTable.add(connection, userBalance2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), BALANCE.toSort()));
            Results<UserBalance> results = userBalanceTable.getAll(connection, new Page(), sort);
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(userBalance2, results.getResults().get(0));
            assertEquals(userBalance1, results.getResults().get(1));
        }
    }

    @Test
    public void testConsumeNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<UserBalance> list = new ArrayList<>();
            assertEquals(0, userBalanceTable.consume(connection, list::add, emptySet()));
            assertTrue(list.isEmpty());
        }
    }

    @Test
    public void testConsumeSomeNoSort() throws SQLException {
        UserBalance userBalance1 = new UserBalance().setUserId(user1.getId()).setBalance(10);
        UserBalance userBalance2 = new UserBalance().setUserId(user2.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.add(connection, userBalance1));
            assertEquals(1, userBalanceTable.add(connection, userBalance2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<UserBalance> list = new ArrayList<>();
            assertEquals(2, userBalanceTable.consume(connection, list::add, emptySet()));
            assertEquals(2, list.size());
            assertEquals(userBalance1, list.get(0));
            assertEquals(userBalance2, list.get(1));
        }
    }

    @Test
    public void testConsumeSomeWithSort() throws SQLException {
        UserBalance userBalance1 = new UserBalance().setUserId(user1.getId()).setBalance(10);
        UserBalance userBalance2 = new UserBalance().setUserId(user2.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.add(connection, userBalance1));
            assertEquals(1, userBalanceTable.add(connection, userBalance2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<UserBalance> list = new ArrayList<>();
            Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), BALANCE.toSort()));
            assertEquals(2, userBalanceTable.consume(connection, list::add, sort));
            assertEquals(2, list.size());
            assertEquals(userBalance2, list.get(0));
            assertEquals(userBalance1, list.get(1));
        }
    }

    @Test
    public void testSetInitialBalanceNoneExists() throws SQLException {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.setInitialBalance(connection, userBalance));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserBalance> fetched = userBalanceTable.get(connection, userBalance.getUserId());
            assertTrue(fetched.isPresent());
            assertEquals(userBalance.getBalance(), fetched.get().getBalance());
        }
    }

    @Test
    public void testSetInitialBalanceAlreadyExists() throws SQLException {
        UserBalance existingBalance = new UserBalance().setUserId(user1.getId()).setBalance(20);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.add(connection, existingBalance));
            connection.commit();
        }
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userBalanceTable.setInitialBalance(connection, userBalance));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserBalance> fetched = userBalanceTable.get(connection, userBalance.getUserId());
            assertTrue(fetched.isPresent());
            assertEquals(existingBalance.getBalance(), fetched.get().getBalance());
        }
    }

    @Test
    public void testAdd() throws SQLException {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.add(connection, userBalance));
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddConflict() throws SQLException {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.add(connection, userBalance));
            userBalanceTable.add(connection, userBalance);
            connection.commit();
        }
    }

    @Test
    public void testUpdateIncrementMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userBalanceTable.update(connection, "missing-id", 10));
            connection.commit();
        }
    }

    @Test
    public void testUpdateIncrement() throws SQLException {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.add(connection, userBalance));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.update(connection, userBalance.getUserId(), 10));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserBalance> updated = userBalanceTable.get(connection, userBalance.getUserId());
            assertTrue(updated.isPresent());
            assertEquals(20, updated.get().getBalance());
        }
    }

    @Test
    public void testUpdateDecrementMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userBalanceTable.update(connection, "missing-id", -10));
        }
    }

    @Test
    public void testUpdateDecrementTooFar() throws SQLException {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.add(connection, userBalance));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userBalanceTable.update(connection, userBalance.getUserId(), -12));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserBalance> fetched = userBalanceTable.get(connection, userBalance.getUserId());
            assertTrue(fetched.isPresent());
            assertEquals(userBalance.getBalance(), fetched.get().getBalance()); // not updated
        }
    }

    @Test
    public void testUpdateDecrement() throws SQLException {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.add(connection, userBalance));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.update(connection, userBalance.getUserId(), -8));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserBalance> fetched = userBalanceTable.get(connection, userBalance.getUserId());
            assertTrue(fetched.isPresent());
            assertEquals(2, fetched.get().getBalance());
        }
    }

    @Test
    public void testUpdateZero() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userBalanceTable.update(connection, user1.getId(), 0));
            connection.commit();
        }
    }

    @Test
    public void testDeleteMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userBalanceTable.delete(connection, "missing"));
            connection.commit();
        }
    }

    @Test
    public void testDelete() throws SQLException {
        UserBalance userBalance = new UserBalance().setUserId(user1.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.add(connection, userBalance));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.delete(connection, userBalance.getUserId()));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userBalanceTable.get(connection, userBalance.getUserId()).isPresent());
        }
    }

    @Test
    public void testTruncate() throws SQLException {
        UserBalance userBalance1 = new UserBalance().setUserId(user1.getId()).setBalance(10);
        UserBalance userBalance2 = new UserBalance().setUserId(user2.getId()).setBalance(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userBalanceTable.add(connection, userBalance1));
            assertEquals(1, userBalanceTable.add(connection, userBalance2));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(2, userBalanceTable.truncate(connection));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserBalance> results = userBalanceTable.getAll(connection, new Page(), emptySet());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}
