package vstocks.model;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.Market.TWITTER;

public class MarketTest {
    @Test
    public void testDisplayName() {
        assertEquals("Twitter", TWITTER.getDisplayName());
    }

    @Test
    public void testFromExactMatch() {
        Optional<Market> market = Market.from("TWITTER");
        assertTrue(market.isPresent());
        assertEquals(TWITTER, market.get());
    }

    @Test
    public void testFromWrongCase() {
        Optional<Market> market = Market.from("twitter");
        assertTrue(market.isPresent());
        assertEquals(TWITTER, market.get());
    }

    @Test
    public void testFromDisplayName() {
        // Note, currently no display name differs from a case-insensitive comparison with the enum name
        Optional<Market> market = Market.from("twitter");
        assertTrue(market.isPresent());
        assertEquals(TWITTER, market.get());
    }
}
