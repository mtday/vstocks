package vstocks.db.impl;

import vstocks.model.Exchange;
import org.junit.*;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class JdbcExchangeStoreIT {
    @Rule
    public DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private JdbcExchangeStore exchangeStore;

    @Before
    public void setup() {
        exchangeStore = new JdbcExchangeStore(dataSourceExternalResource.get());
    }

    @After
    public void cleanup() {
        exchangeStore.truncate();
    }

    @Test
    public void testGetMissing() {
        assertFalse(exchangeStore.get("missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        Exchange exchange = new Exchange().setId("id").setName("name");
        assertEquals(1, exchangeStore.add(exchange));

        Optional<Exchange> fetched = exchangeStore.get(exchange.getId());
        assertTrue(fetched.isPresent());
        assertEquals(exchange, fetched.get());
    }

    @Test
    public void testGetAllNone() {
        assertTrue(exchangeStore.getAll().isEmpty());
    }

    @Test
    public void testGetAllSome() {
        Exchange exchange1 = new Exchange().setId("id1").setName("name");
        Exchange exchange2 = new Exchange().setId("id2").setName("name");
        assertEquals(2, exchangeStore.add(exchange1, exchange2));

        List<Exchange> fetched = exchangeStore.getAll();
        assertEquals(2, fetched.size());
        assertTrue(fetched.contains(exchange1));
        assertTrue(fetched.contains(exchange2));
    }

    @Test
    public void testAdd() {
        Exchange exchange1 = new Exchange().setId("id1").setName("name");
        assertEquals(1, exchangeStore.add(exchange1));

        Exchange exchange2 = new Exchange().setId("id2").setName("name");
        Exchange exchange3 = new Exchange().setId("id3").setName("name");
        assertEquals(2, exchangeStore.add(exchange2, exchange3));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        Exchange exchange = new Exchange().setId("id").setName("name");
        assertEquals(1, exchangeStore.add(exchange));
        assertEquals(1, exchangeStore.add(exchange));
    }

    @Test
    public void testUpdateMissing() {
        Exchange exchange = new Exchange().setId("id").setName("name");
        assertEquals(0, exchangeStore.update(exchange));
    }

    @Test
    public void testUpdate() {
        Exchange exchange = new Exchange().setId("id").setName("name");
        assertEquals(1, exchangeStore.add(exchange));

        exchange.setName("updated");
        assertEquals(1, exchangeStore.update(exchange));

        Optional<Exchange> updated = exchangeStore.get(exchange.getId());
        assertTrue(updated.isPresent());
        assertEquals(exchange, updated.get());
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, exchangeStore.delete("missing"));
    }

    @Test
    public void testDelete() {
        Exchange exchange = new Exchange().setId("id").setName("name");
        assertEquals(1, exchangeStore.add(exchange));
        assertEquals(1, exchangeStore.delete(exchange.getId()));

        assertFalse(exchangeStore.get(exchange.getId()).isPresent());
    }

    @Test
    public void testTruncate() {
        Exchange exchange1 = new Exchange().setId("id1").setName("name");
        Exchange exchange2 = new Exchange().setId("id2").setName("name");
        assertEquals(2, exchangeStore.add(exchange1, exchange2));
        assertEquals(2, exchangeStore.truncate());

        assertTrue(exchangeStore.getAll().isEmpty());
    }
}
