package vstocks.model;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static vstocks.model.DeltaInterval.DAY3;
import static vstocks.model.DeltaInterval.DAY30;

public class DeltaIntervalTest {
    @Test
    public void testFromName() {
        Optional<DeltaInterval> interval = DeltaInterval.from(DAY3.name());
        assertTrue(interval.isPresent());
        assertEquals(DAY3, interval.get());
    }

    @Test
    public void testFromDisplayName() {
        Optional<DeltaInterval> interval = DeltaInterval.from(DAY3.getDisplayName());
        assertTrue(interval.isPresent());
        assertEquals(DAY3, interval.get());
    }

    @Test
    public void testFromMissing() {
        assertFalse(DeltaInterval.from("missing").isPresent());
    }

    @Test
    public void testEarliest() {
        for (DeltaInterval interval : DeltaInterval.values()) {
            assertNotNull(interval.getEarliest());
        }
    }

    @Test
    public void testGetLast() {
        assertEquals(DAY30, DeltaInterval.getLast());
    }
}
