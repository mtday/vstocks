package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
        Map<DeltaInterval, Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        Arrays.stream(DeltaInterval.values())
                .map(interval -> getDelta(interval, 0, 0f))
                .forEach(delta -> {
                    assertTrue(deltas.containsKey(delta.getInterval()));
                    assertEquals(delta, deltas.get(delta.getInterval()));
                });
    }

    @Test
    public void testGetDeltasListOfOneNow() {
        List<Entry<Instant, Long>> entries = singletonList(new SimpleEntry<>(Instant.now(), 10L));
        Map<DeltaInterval, Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        Arrays.stream(DeltaInterval.values())
                .map(interval -> getDelta(interval, 0, 0f))
                .forEach(delta -> {
                    assertTrue(deltas.containsKey(delta.getInterval()));
                    assertEquals(delta, deltas.get(delta.getInterval()));
                });
    }

    @Test
    public void testGetDeltasListOfOneTooOld() {
        Instant days45ago = Instant.now().truncatedTo(ChronoUnit.DAYS).minusSeconds(DAYS.toSeconds(45));
        List<Entry<Instant, Long>> entries = singletonList(new SimpleEntry<>(days45ago, 10L));
        Map<DeltaInterval, Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        Arrays.stream(DeltaInterval.values())
                .map(interval -> getDelta(interval, 0, 0f))
                .forEach(delta -> {
                    assertTrue(deltas.containsKey(delta.getInterval()));
                    assertEquals(delta, deltas.get(delta.getInterval()));
                });
    }

    @Test
    public void testGetDeltasListOnlyRecent() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(25)
                .map(i -> new SimpleEntry<>(now.minusSeconds(HOURS.toSeconds(i)), 300L - (i * 10)))
                .collect(toList());
        Map<DeltaInterval, Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        assertEquals(getDelta(HOUR6, 50, 20.833332f), deltas.get(HOUR6));
        assertEquals(getDelta(HOUR12, 110, 61.11111f), deltas.get(HOUR12));
        assertEquals(getDelta(DAY1, 230, 383.3333f), deltas.get(DAY1));
        assertEquals(getDelta(DAY3, 240, 480.00003f), deltas.get(DAY3));
        assertEquals(getDelta(DAY7, 240, 480.00003f), deltas.get(DAY7));
        assertEquals(getDelta(DAY14, 240, 480.00003f), deltas.get(DAY14));
        assertEquals(getDelta(DAY30, 240, 480.00003f), deltas.get(DAY30));
    }

    @Test
    public void testGetDeltasListOnlyOld() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(25)
                .map(i -> new SimpleEntry<>(now.minusSeconds(DAYS.toSeconds(5) + HOURS.toSeconds(i)), 300L - (i * 10)))
                .collect(toList());
        Map<DeltaInterval, Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        assertEquals(getDelta(HOUR6, 0, 0f), deltas.get(HOUR6));
        assertEquals(getDelta(HOUR12, 0, 0f), deltas.get(HOUR12));
        assertEquals(getDelta(DAY1, 0, 0f), deltas.get(DAY1));
        assertEquals(getDelta(DAY3, 0, 0f), deltas.get(DAY3));
        assertEquals(getDelta(DAY7, 240, 480.00003f), deltas.get(DAY7));
        assertEquals(getDelta(DAY14, 240, 480.00003f), deltas.get(DAY14));
        assertEquals(getDelta(DAY30, 240, 480.00003f), deltas.get(DAY30));
    }

    @Test
    public void testGetDeltasListFullyPopulatedNegativeChange() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(90 * 6)
                .map(i -> new SimpleEntry<>(now.minusSeconds(HOURS.toSeconds(4 * i)), 300L + i))
                .collect(toList());
        Map<DeltaInterval, Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        assertEquals(getDelta(HOUR6, 0, 0f), deltas.get(HOUR6));
        assertEquals(getDelta(HOUR12, -2, -0.660066f), deltas.get(HOUR12));
        assertEquals(getDelta(DAY1, -5, -1.6339871f), deltas.get(DAY1));
        assertEquals(getDelta(DAY3, -17, -5.345912f), deltas.get(DAY3));
        assertEquals(getDelta(DAY7, -41, -11.988304f), deltas.get(DAY7));
        assertEquals(getDelta(DAY14, -83, -21.614582f), deltas.get(DAY14));
        assertEquals(getDelta(DAY30, -179, -37.291668f), deltas.get(DAY30));
    }

    @Test
    public void testGettersAndSetters() {
        Delta delta = new Delta().setInterval(DAY3).setChange(5).setPercent(5.25f);

        assertEquals(DAY3, delta.getInterval());
        assertEquals(5, delta.getChange());
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
