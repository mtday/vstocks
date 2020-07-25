package vstocks.db.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import vstocks.model.Exchange;
import vstocks.model.Symbol;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class JdbcSymbolStoreIT {
    @Rule
    public DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private JdbcExchangeStore exchangeStore;
    private JdbcSymbolStore symbolStore;

    private final Exchange exchange1 = new Exchange().setId("id1").setName("name1");
    private final Exchange exchange2 = new Exchange().setId("id2").setName("name2");

    @Before
    public void setup() {
        exchangeStore = new JdbcExchangeStore(dataSourceExternalResource.get());
        symbolStore = new JdbcSymbolStore(dataSourceExternalResource.get());

        exchangeStore.add(exchange1, exchange2);
    }

    @After
    public void cleanup() {
        symbolStore.truncate();
        exchangeStore.truncate();
    }

    @Test
    public void testGetMissingExchange() {
        assertFalse(symbolStore.get("missing-id", "missing-id").isPresent());
    }

    @Test
    public void testGetMissingSymbol() {
        assertFalse(symbolStore.get(exchange1.getId(), "missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        Symbol symbol = new Symbol().setExchangeId(exchange1.getId()).setId("id").setSymbol("symbol").setName("name");
        assertEquals(1, symbolStore.add(symbol));

        Optional<Symbol> fetched = symbolStore.get(symbol.getExchangeId(), symbol.getId());
        assertTrue(fetched.isPresent());
        assertEquals(symbol, fetched.get());
    }

    @Test
    public void testGetAllForExchange() {
        assertTrue(symbolStore.getAll(exchange1.getId()).isEmpty());
    }

    @Test
    public void testGetAllForExchangeSome() {
        Symbol symbol1 = new Symbol().setExchangeId(exchange1.getId()).setId("id1").setSymbol("sym1").setName("name1");
        Symbol symbol2 = new Symbol().setExchangeId(exchange1.getId()).setId("id2").setSymbol("sym2").setName("name2");
        assertEquals(2, symbolStore.add(symbol1, symbol2));

        List<Symbol> fetched = symbolStore.getAll(exchange1.getId());
        assertEquals(2, fetched.size());
        assertTrue(fetched.contains(symbol1));
        assertTrue(fetched.contains(symbol2));
    }

    @Test
    public void testGetAllNone() {
        assertTrue(symbolStore.getAll().isEmpty());
    }

    @Test
    public void testGetAllSome() {
        Symbol symbol1 = new Symbol().setExchangeId(exchange1.getId()).setId("id1").setSymbol("sym1").setName("name1");
        Symbol symbol2 = new Symbol().setExchangeId(exchange2.getId()).setId("id2").setSymbol("sym2").setName("name2");
        assertEquals(2, symbolStore.add(symbol1, symbol2));

        List<Symbol> fetched = symbolStore.getAll();
        assertEquals(2, fetched.size());
        assertTrue(fetched.contains(symbol1));
        assertTrue(fetched.contains(symbol2));
    }

    @Test
    public void testAdd() {
        Symbol symbol1 = new Symbol().setExchangeId(exchange1.getId()).setId("id1").setSymbol("sym1").setName("name1");
        assertEquals(1, symbolStore.add(symbol1));

        Symbol symbol2 = new Symbol().setExchangeId(exchange2.getId()).setId("id2").setSymbol("sym2").setName("name2");
        Symbol symbol3 = new Symbol().setExchangeId(exchange2.getId()).setId("id3").setSymbol("sym3").setName("name3");
        assertEquals(2, symbolStore.add(symbol2, symbol3));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        Symbol symbol = new Symbol().setExchangeId(exchange1.getId()).setId("id").setSymbol("sym").setName("name");
        assertEquals(1, symbolStore.add(symbol));
        assertEquals(1, symbolStore.add(symbol));
    }

    @Test
    public void testUpdateMissing() {
        Symbol symbol = new Symbol().setExchangeId(exchange1.getId()).setId("id").setSymbol("sym").setName("name");
        assertEquals(0, symbolStore.update(symbol));
    }

    @Test
    public void testUpdate() {
        Symbol symbol = new Symbol().setExchangeId(exchange1.getId()).setId("id").setSymbol("sym").setName("name");
        assertEquals(1, symbolStore.add(symbol));

        symbol.setSymbol("updated");
        symbol.setName("updated");
        assertEquals(1, symbolStore.update(symbol));

        Optional<Symbol> updated = symbolStore.get(symbol.getExchangeId(), symbol.getId());
        assertTrue(updated.isPresent());
        assertEquals(symbol, updated.get());
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, symbolStore.delete(exchange1.getId(), "missing-id"));
    }

    @Test
    public void testDelete() {
        Symbol symbol = new Symbol().setExchangeId(exchange1.getId()).setId("id").setSymbol("sym").setName("name");
        assertEquals(1, symbolStore.add(symbol));
        assertEquals(1, symbolStore.delete(symbol.getExchangeId(), symbol.getId()));

        assertFalse(symbolStore.get(symbol.getExchangeId(), symbol.getId()).isPresent());
    }

    @Test
    public void testTruncate() {
        Symbol symbol1 = new Symbol().setExchangeId(exchange1.getId()).setId("id1").setSymbol("sym1").setName("name1");
        Symbol symbol2 = new Symbol().setExchangeId(exchange2.getId()).setId("id2").setSymbol("sym2").setName("name2");
        assertEquals(2, symbolStore.add(symbol1, symbol2));
        assertEquals(2, symbolStore.truncate());

        assertTrue(symbolStore.getAll().isEmpty());
    }
}
