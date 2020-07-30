package vstocks.service.db.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.service.db.DataSourceExternalResource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class JdbcMarketTableIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private MarketTable marketTable;

    @Before
    public void setup() {
        marketTable = new MarketTable();
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            marketTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(marketTable.get(connection, "missing-id").isPresent());
        }
    }

    @Test
    public void testGetExists() throws SQLException {
        Market market = new Market().setId("id").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketTable.add(connection, market));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Market> fetched = marketTable.get(connection, market.getId());
            assertTrue(fetched.isPresent());
            assertEquals(market, fetched.get());
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Market> results = marketTable.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSome() throws SQLException {
        Market market1 = new Market().setId("id1").setName("name");
        Market market2 = new Market().setId("id2").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketTable.add(connection, market1));
            assertEquals(1, marketTable.add(connection, market2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Market> results = marketTable.getAll(connection, new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(market1));
            assertTrue(results.getResults().contains(market2));
        }
    }

    @Test
    public void testConsumeNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Market> list = new ArrayList<>();
            assertEquals(0, marketTable.consume(connection, list::add));
            assertTrue(list.isEmpty());
        }
    }

    @Test
    public void testConsumeSome() throws SQLException {
        Market market1 = new Market().setId("id1").setName("name");
        Market market2 = new Market().setId("id2").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketTable.add(connection, market1));
            assertEquals(1, marketTable.add(connection, market2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Market> list = new ArrayList<>();
            assertEquals(2, marketTable.consume(connection, list::add));
            assertEquals(2, list.size());
            assertTrue(list.contains(market1));
            assertTrue(list.contains(market2));
        }
    }

    @Test
    public void testAdd() throws SQLException {
        Market market = new Market().setId("id").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketTable.add(connection, market));
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddConflict() throws SQLException {
        Market market = new Market().setId("id").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketTable.add(connection, market));
            marketTable.add(connection, market);
            connection.commit();
        }
    }

    @Test
    public void testUpdateMissing() throws SQLException {
        Market market = new Market().setId("id").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, marketTable.update(connection, market));
            connection.commit();
        }
    }

    @Test
    public void testUpdateNoChange() throws SQLException {
        Market market = new Market().setId("id").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketTable.add(connection, market));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, marketTable.update(connection, market));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Market> updated = marketTable.get(connection, market.getId());
            assertTrue(updated.isPresent());
            assertEquals(market.getName(), updated.get().getName());
        }
    }

    @Test
    public void testUpdate() throws SQLException {
        Market market = new Market().setId("id").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketTable.add(connection, market));
            connection.commit();
        }
        market.setName("updated");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketTable.update(connection, market));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Market> updated = marketTable.get(connection, market.getId());
            assertTrue(updated.isPresent());
            assertEquals(market.getName(), updated.get().getName());
        }
    }

    @Test
    public void testDeleteMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, marketTable.delete(connection, "missing"));
        }
    }

    @Test
    public void testDelete() throws SQLException {
        Market market = new Market().setId("id").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketTable.add(connection, market));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketTable.delete(connection, market.getId()));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(marketTable.get(connection, market.getId()).isPresent());
        }
    }

    @Test
    public void testTruncate() throws SQLException {
        Market market1 = new Market().setId("id1").setName("name");
        Market market2 = new Market().setId("id2").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketTable.add(connection, market1));
            assertEquals(1, marketTable.add(connection, market2));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(2, marketTable.truncate(connection));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Market> results = marketTable.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}
