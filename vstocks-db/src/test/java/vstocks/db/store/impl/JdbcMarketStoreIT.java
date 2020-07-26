package vstocks.db.store.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DataSourceExternalResource;
import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.*;

public class JdbcMarketStoreIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private JdbcMarketStore marketStore;

    @Before
    public void setup() {
        marketStore = new JdbcMarketStore();
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            marketStore.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(marketStore.get(connection, "missing-id").isPresent());
        }
    }

    @Test
    public void testGetExists() throws SQLException {
        Market market = new Market().setId("id").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketStore.add(connection, market));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Market> fetched = marketStore.get(connection, market.getId());
            assertTrue(fetched.isPresent());
            assertEquals(market, fetched.get());
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Market> results = marketStore.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSome() throws SQLException {
        Market market1 = new Market().setId("id1").setName("name");
        Market market2 = new Market().setId("id2").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketStore.add(connection, market1));
            assertEquals(1, marketStore.add(connection, market2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Market> results = marketStore.getAll(connection, new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(market1));
            assertTrue(results.getResults().contains(market2));
        }
    }

    @Test
    public void testAdd() throws SQLException {
        Market market = new Market().setId("id").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketStore.add(connection, market));
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddConflict() throws SQLException {
        Market market = new Market().setId("id").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketStore.add(connection, market));
            marketStore.add(connection, market);
            connection.commit();
        }
    }

    @Test
    public void testUpdateMissing() throws SQLException {
        Market market = new Market().setId("id").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, marketStore.update(connection, market));
            connection.commit();
        }
    }

    @Test
    public void testUpdate() throws SQLException {
        Market market = new Market().setId("id").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketStore.add(connection, market));
            connection.commit();
        }

        market.setName("updated");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketStore.update(connection, market));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Market> updated = marketStore.get(connection, market.getId());
            assertTrue(updated.isPresent());
            assertEquals(market, updated.get());
        }
    }

    @Test
    public void testDeleteMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, marketStore.delete(connection, "missing"));
        }
    }

    @Test
    public void testDelete() throws SQLException {
        Market market = new Market().setId("id").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketStore.add(connection, market));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketStore.delete(connection, market.getId()));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(marketStore.get(connection, market.getId()).isPresent());
        }
    }

    @Test
    public void testTruncate() throws SQLException {
        Market market1 = new Market().setId("id1").setName("name");
        Market market2 = new Market().setId("id2").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketStore.add(connection, market1));
            assertEquals(1, marketStore.add(connection, market2));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(2, marketStore.truncate(connection));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Market> results = marketStore.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}
