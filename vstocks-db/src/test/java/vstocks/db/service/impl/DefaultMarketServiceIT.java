package vstocks.db.service.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.store.impl.JdbcMarketStore;
import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.*;

public class DefaultMarketServiceIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private JdbcMarketStore marketStore;
    private DefaultMarketService marketService;

    @Before
    public void setup() {
        marketStore = new JdbcMarketStore();
        marketService = new DefaultMarketService(dataSourceExternalResource.get(), marketStore);
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            marketStore.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() {
        assertFalse(marketService.get("missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        Market market = new Market().setId("id").setName("name");
        assertEquals(1, marketService.add(market));

        Optional<Market> fetched = marketService.get(market.getId());
        assertTrue(fetched.isPresent());
        assertEquals(market, fetched.get());
    }

    @Test
    public void testGetAllNone() {
        Results<Market> results = marketService.getAll(new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSome() {
        Market market1 = new Market().setId("id1").setName("name");
        Market market2 = new Market().setId("id2").setName("name");
        assertEquals(1, marketService.add(market1));
        assertEquals(1, marketService.add(market2));

        Results<Market> results = marketService.getAll(new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(market1));
        assertTrue(results.getResults().contains(market2));
    }

    @Test
    public void testAdd() {
        Market market = new Market().setId("id").setName("name");
        assertEquals(1, marketService.add(market));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        Market market = new Market().setId("id").setName("name");
        assertEquals(1, marketService.add(market));
        marketService.add(market);
    }

    @Test
    public void testUpdateMissing() {
        Market market = new Market().setId("id").setName("name");
        assertEquals(0, marketService.update(market));
    }

    @Test
    public void testUpdate() {
        Market market = new Market().setId("id").setName("name");
        assertEquals(1, marketService.add(market));

        market.setName("updated");
        assertEquals(1, marketService.update(market));

        Optional<Market> updated = marketService.get(market.getId());
        assertTrue(updated.isPresent());
        assertEquals(market, updated.get());
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, marketService.delete("missing"));
    }

    @Test
    public void testDelete() {
        Market market = new Market().setId("id").setName("name");
        assertEquals(1, marketService.add(market));
        assertEquals(1, marketService.delete(market.getId()));
        assertFalse(marketService.get(market.getId()).isPresent());
    }
}
