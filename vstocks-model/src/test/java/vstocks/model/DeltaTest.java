package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;
import static vstocks.model.DeltaInterval.*;

public class DeltaTest {
    private Delta getDelta(DeltaInterval interval, int change, float percent) {
        return new Delta().setInterval(interval).setChange(change).setPercent(percent);
    }

    @Test
    public void testGetDeltasEmptyList() {
        List<Entry<Instant, Long>> entries = emptyList();
        List<Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        List<Delta> expected = Arrays.stream(DeltaInterval.values())
                .map(interval -> getDelta(interval, 0, 0f))
                .collect(toList());
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetDeltasListOfOneNow() {
        List<Entry<Instant, Long>> entries = singletonList(new SimpleEntry<>(Instant.now(), 10L));
        List<Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        List<Delta> expected = Arrays.stream(DeltaInterval.values())
                .map(interval -> getDelta(interval, 0, 0f))
                .collect(toList());
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetDeltasListOfOneTooOld() {
        Instant days45ago = Instant.now().truncatedTo(ChronoUnit.DAYS).minusSeconds(DAYS.toSeconds(45));
        List<Entry<Instant, Long>> entries = singletonList(new SimpleEntry<>(days45ago, 10L));
        List<Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        List<Delta> expected = Arrays.stream(DeltaInterval.values())
                .map(interval -> getDelta(interval, 0, 0f))
                .collect(toList());
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetDeltasListOnlyRecent() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(25)
                .map(i -> new SimpleEntry<>(now.minusSeconds(HOURS.toSeconds(i)), 300L - (i * 10)))
                .collect(toList());
        List<Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        assertTrue(deltas.contains(getDelta(HOUR6, 50, 20.833332f)));
        assertTrue(deltas.contains(getDelta(HOUR12, 110, 61.11111f)));
        assertTrue(deltas.contains(getDelta(DAY1, 230, 383.3333f)));
        assertTrue(deltas.contains(getDelta(DAY3, 240, 480.00003f)));
        assertTrue(deltas.contains(getDelta(DAY7, 240, 480.00003f)));
        assertTrue(deltas.contains(getDelta(DAY14, 240, 480.00003f)));
        assertTrue(deltas.contains(getDelta(DAY30, 240, 480.00003f)));
    }

    @Test
    public void testGetDeltasListOnlyOld() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(25)
                .map(i -> new SimpleEntry<>(now.minusSeconds(DAYS.toSeconds(5) + HOURS.toSeconds(i)), 300L - (i * 10)))
                .collect(toList());
        List<Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        assertTrue(deltas.contains(getDelta(HOUR6, 0, 0f)));
        assertTrue(deltas.contains(getDelta(HOUR12, 0, 0f)));
        assertTrue(deltas.contains(getDelta(DAY1, 0, 0f)));
        assertTrue(deltas.contains(getDelta(DAY3, 0, 0f)));
        assertTrue(deltas.contains(getDelta(DAY7, 240, 480.00003f)));
        assertTrue(deltas.contains(getDelta(DAY14, 240, 480.00003f)));
        assertTrue(deltas.contains(getDelta(DAY30, 240, 480.00003f)));
    }

    @Test
    public void testGetDeltasListFullyPopulatedNegativeChange() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(90 * 6)
                .map(i -> new SimpleEntry<>(now.minusSeconds(HOURS.toSeconds(4 * i)), 300L + i))
                .collect(toList());
        List<Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        assertTrue(deltas.contains(getDelta(HOUR6, 0, 0f)));
        assertTrue(deltas.contains(getDelta(HOUR12, -2, -0.660066f)));
        assertTrue(deltas.contains(getDelta(DAY1, -5, -1.6339871f)));
        assertTrue(deltas.contains(getDelta(DAY3, -17, -5.345912f)));
        assertTrue(deltas.contains(getDelta(DAY7, -41, -11.988304f)));
        assertTrue(deltas.contains(getDelta(DAY14, -83, -21.614582f)));
        assertTrue(deltas.contains(getDelta(DAY30, -179, -37.291668f)));
    }

    @Test
    public void testGettersAndSetters() {
        Delta delta = new Delta().setInterval(DAY3).setChange(5).setPercent(5.25f);

        assertEquals(DAY3, delta.getInterval());
        assertEquals(5, delta.getChange(), 0.001);
        assertEquals(5.25f, delta.getPercent(), 0.001);
    }

    @Test
    public void testEquals() {
        Delta page1 = new Delta().setInterval(DAY3).setChange(5).setPercent(5.25f);
        Delta page2 = new Delta().setInterval(DAY3).setChange(5).setPercent(5.25f);
        assertEquals(page1, page2);
    }

    @Test
    public void testHashCode() {
        Delta delta = new Delta().setInterval(DAY3).setChange(5).setPercent(5.25f);
        assertEquals(29791, new Delta().hashCode());
        assertNotEquals(0, delta.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        Delta delta = new Delta().setInterval(DAY3).setChange(5).setPercent(5.25f);
        assertEquals("Delta{interval=3d, change=5, percent=5.25}", delta.toString());
    }
}
