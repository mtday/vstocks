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
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.DeltaInterval.*;

public class DeltaTest {
    private Delta getDelta(DeltaInterval interval, Long first, Long last, long change, float percent) {
        return new Delta().setInterval(interval).setOldest(first).setNewest(last).setChange(change).setPercent(percent);
    }

    @Test
    public void testGetDeltasEmptyList() {
        List<Entry<Instant, Long>> entries = emptyList();
        List<Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        List<Delta> expected = Arrays.stream(DeltaInterval.values())
                .map(interval -> getDelta(interval, null, null, 0, 0f))
                .collect(toList());
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetDeltasListOfOneNow() {
        List<Entry<Instant, Long>> entries = singletonList(new SimpleEntry<>(Instant.now(), 10L));
        List<Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        List<Delta> expected = Arrays.stream(DeltaInterval.values())
                .map(interval -> getDelta(interval, 10L, 10L, 0, 0f))
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
                .map(interval -> getDelta(interval, null, null, 0, 0f))
                .collect(toList());
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetDeltasListOnlyRecentGettingWorse() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        // These values are getting lower over time, which means getting worse for normal deltas
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(25)
                .map(i -> new SimpleEntry<>(now.minusSeconds(HOURS.toSeconds(i)), 10L + (i * 10)))
                .collect(toList());
        List<Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);
        List<Delta> expected = asList(
                getDelta(HOUR6,   70L, 20L,  -50, -71.42857f),
                getDelta(HOUR12, 130L, 20L, -110, -84.61539f),
                getDelta(DAY1,   250L, 20L, -230, -92.0f),
                getDelta(DAY3,   260L, 20L, -240, -92.30769f),
                getDelta(DAY7,   260L, 20L, -240, -92.30769f),
                getDelta(DAY14,  260L, 20L, -240, -92.30769f),
                getDelta(DAY30,  260L, 20L, -240, -92.30769f)
        );
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetDeltasListOnlyRecentGettingBetter() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        // These values are getting higher over time, which means getting better for normal deltas
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(25)
                .map(i -> new SimpleEntry<>(now.minusSeconds(HOURS.toSeconds(i)), 300L - (i * 10)))
                .collect(toList());
        List<Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);
        List<Delta> expected = asList(
                getDelta(HOUR6,  240L, 290L,  50,  20.833332f),
                getDelta(HOUR12, 180L, 290L, 110,  61.11111f),
                getDelta(DAY1,    60L, 290L, 230, 383.3333f),
                getDelta(DAY3,    50L, 290L, 240, 480.00003f),
                getDelta(DAY7,    50L, 290L, 240, 480.00003f),
                getDelta(DAY14,   50L, 290L, 240, 480.00003f),
                getDelta(DAY30,   50L, 290L, 240, 480.00003f)
        );
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetDeltasListOnlyOldGettingWorse() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        // These values are getting lower over time, which means getting worse for normal deltas
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(25)
                .map(i -> new SimpleEntry<>(now.minusSeconds(DAYS.toSeconds(5) + HOURS.toSeconds(i)), 10L + (i * 10)))
                .collect(toList());
        List<Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);
        List<Delta> expected = asList(
                getDelta(HOUR6,  null, null,    0,   0.0f),
                getDelta(HOUR12, null, null,    0,   0.0f),
                getDelta(DAY1,   null, null,    0,   0.0f),
                getDelta(DAY3,   null, null,    0,   0.0f),
                getDelta(DAY7,   260L,  20L, -240, -92.30769f),
                getDelta(DAY14,  260L,  20L, -240, -92.30769f),
                getDelta(DAY30,  260L,  20L, -240, -92.30769f)
        );
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetDeltasListOnlyOldGettingBetter() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        // These values are getting higher over time, which means getting better for normal deltas
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(25)
                .map(i -> new SimpleEntry<>(now.minusSeconds(DAYS.toSeconds(5) + HOURS.toSeconds(i)), 300L - (i * 10)))
                .collect(toList());
        List<Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);
        List<Delta> expected = asList(
                getDelta(HOUR6,  null, null,   0,   0.0f),
                getDelta(HOUR12, null, null,   0,   0.0f),
                getDelta(DAY1,   null, null,   0,   0.0f),
                getDelta(DAY3,   null, null,   0,   0.0f),
                getDelta(DAY7,    50L, 290L, 240, 480.00003f),
                getDelta(DAY14,   50L, 290L, 240, 480.00003f),
                getDelta(DAY30,   50L, 290L, 240, 480.00003f)
        );
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetDeltasListFullyPopulatedGettingWorse() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        // These values are getting lower over time, which means getting worse for normal deltas
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(90 * 12)
                .map(i -> new SimpleEntry<>(now.minusSeconds(HOURS.toSeconds(2 * i)), 10L + i))
                .collect(toList());
        List<Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);
        List<Delta> expected = asList(
                getDelta(HOUR6,   13L, 11L,   -2, -15.384616f),
                getDelta(HOUR12,  16L, 11L,   -5, -31.25f),
                getDelta(DAY1,    22L, 11L,  -11, -50.0f),
                getDelta(DAY3,    46L, 11L,  -35, -76.08696f),
                getDelta(DAY7,    94L, 11L,  -83, -88.297874f),
                getDelta(DAY14,  178L, 11L, -167, -93.82023f),
                getDelta(DAY30,  370L, 11L, -359, -97.02703f)
        );
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetDeltasListFullyPopulatedGettingBetter() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        // These values are getting higher over time, which means getting better for normal deltas
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(90 * 12)
                .map(i -> new SimpleEntry<>(now.minusSeconds(HOURS.toSeconds(2 * i)), 500L - i))
                .collect(toList());
        List<Delta> deltas = Delta.getDeltas(entries, Entry::getKey, Entry::getValue);
        List<Delta> expected = asList(
                getDelta(HOUR6,  497L, 499L,   2,   0.40241447f),
                getDelta(HOUR12, 494L, 499L,   5,   1.0121458f),
                getDelta(DAY1,   488L, 499L,  11,   2.2540982f),
                getDelta(DAY3,   464L, 499L,  35,   7.543103f),
                getDelta(DAY7,   416L, 499L,  83,  19.951923f),
                getDelta(DAY14,  332L, 499L, 167,  50.301205f),
                getDelta(DAY30,  140L, 499L, 359, 256.4286f)
        );
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetReverseDeltasEmptyList() {
        List<Entry<Instant, Long>> entries = emptyList();
        List<Delta> deltas = Delta.getReverseDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        List<Delta> expected = Arrays.stream(DeltaInterval.values())
                .map(interval -> getDelta(interval, null, null, 0, 0f))
                .collect(toList());
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetReverseDeltasListOfOneNow() {
        List<Entry<Instant, Long>> entries = singletonList(new SimpleEntry<>(Instant.now(), 10L));
        List<Delta> deltas = Delta.getReverseDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        List<Delta> expected = Arrays.stream(DeltaInterval.values())
                .map(interval -> getDelta(interval, 10L, 10L, 0, 0f))
                .collect(toList());
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetReverseDeltasListOfOneTooOld() {
        Instant days45ago = Instant.now().truncatedTo(ChronoUnit.DAYS).minusSeconds(DAYS.toSeconds(45));
        List<Entry<Instant, Long>> entries = singletonList(new SimpleEntry<>(days45ago, 10L));
        List<Delta> deltas = Delta.getReverseDeltas(entries, Entry::getKey, Entry::getValue);

        assertEquals(DeltaInterval.values().length, deltas.size());
        List<Delta> expected = Arrays.stream(DeltaInterval.values())
                .map(interval -> getDelta(interval, null, null, 0, 0f))
                .collect(toList());
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetReverseDeltasListOnlyRecentGettingWorse() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        // These values are getting higher over time, which means getting worse for reverse deltas
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(25)
                .map(i -> new SimpleEntry<>(now.minusSeconds(HOURS.toSeconds(i)), 300L - (i * 10)))
                .collect(toList());
        List<Delta> deltas = Delta.getReverseDeltas(entries, Entry::getKey, Entry::getValue);
        List<Delta> expected = asList(
                getDelta(HOUR6,  240L, 290L,  -50,  -20.833332f),
                getDelta(HOUR12, 180L, 290L, -110,  -61.11111f),
                getDelta(DAY1,    60L, 290L, -230, -383.3333f),
                getDelta(DAY3,    50L, 290L, -240, -480.00003f),
                getDelta(DAY7,    50L, 290L, -240, -480.00003f),
                getDelta(DAY14,   50L, 290L, -240, -480.00003f),
                getDelta(DAY30,   50L, 290L, -240, -480.00003f)
        );
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetReverseDeltasListOnlyRecentGettingBetter() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        // These values are getting lower over time, which means getting better for reverse deltas
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(25)
                .map(i -> new SimpleEntry<>(now.minusSeconds(HOURS.toSeconds(i)), 10L + (i * 10)))
                .collect(toList());
        List<Delta> deltas = Delta.getReverseDeltas(entries, Entry::getKey, Entry::getValue);
        List<Delta> expected = asList(
                getDelta(HOUR6,   70L, 20L,  50, 71.42857f),
                getDelta(HOUR12, 130L, 20L, 110, 84.61539f),
                getDelta(DAY1,   250L, 20L, 230, 92.0f),
                getDelta(DAY3,   260L, 20L, 240, 92.30769f),
                getDelta(DAY7,   260L, 20L, 240, 92.30769f),
                getDelta(DAY14,  260L, 20L, 240, 92.30769f),
                getDelta(DAY30,  260L, 20L, 240, 92.30769f)
        );
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetReverseDeltasListOnlyOldGettingWorse() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        // These values are getting higher over time, which means getting worse for reverse deltas
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(25)
                .map(i -> new SimpleEntry<>(now.minusSeconds(DAYS.toSeconds(5) + HOURS.toSeconds(i)), 300L - (i * 10)))
                .collect(toList());
        List<Delta> deltas = Delta.getReverseDeltas(entries, Entry::getKey, Entry::getValue);
        List<Delta> expected = asList(
                getDelta(HOUR6,  null, null,    0,    0.0f),
                getDelta(HOUR12, null, null,    0,    0.0f),
                getDelta(DAY1,   null, null,    0,    0.0f),
                getDelta(DAY3,   null, null,    0,    0.0f),
                getDelta(DAY7,    50L, 290L, -240, -480.00003f),
                getDelta(DAY14,   50L, 290L, -240, -480.00003f),
                getDelta(DAY30,   50L, 290L, -240, -480.00003f)
        );
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetReverseDeltasListOnlyOldGettingBetter() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        // These values are getting lower over time, which means getting better for reverse deltas
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(25)
                .map(i -> new SimpleEntry<>(now.minusSeconds(DAYS.toSeconds(5) + HOURS.toSeconds(i)), 10L + (i * 10)))
                .collect(toList());
        List<Delta> deltas = Delta.getReverseDeltas(entries, Entry::getKey, Entry::getValue);
        List<Delta> expected = asList(
                getDelta(HOUR6,  null, null,   0,  0.0f),
                getDelta(HOUR12, null, null,   0,  0.0f),
                getDelta(DAY1,   null, null,   0,  0.0f),
                getDelta(DAY3,   null, null,   0,  0.0f),
                getDelta(DAY7,   260L,  20L, 240, 92.30769f),
                getDelta(DAY14,  260L,  20L, 240, 92.30769f),
                getDelta(DAY30,  260L,  20L, 240, 92.30769f)
        );
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetReverseDeltasListFullyPopulatedGettingWorse() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        // These values are getting higher over time, which means getting worse for reverse deltas
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(90 * 12)
                .map(i -> new SimpleEntry<>(now.minusSeconds(HOURS.toSeconds(2 * i)), 500L - i))
                .collect(toList());
        List<Delta> deltas = Delta.getReverseDeltas(entries, Entry::getKey, Entry::getValue);
        List<Delta> expected = asList(
                getDelta(HOUR6,  497L, 499L,   -2,   -0.40241447f),
                getDelta(HOUR12, 494L, 499L,   -5,   -1.0121458f),
                getDelta(DAY1,   488L, 499L,  -11,   -2.2540982f),
                getDelta(DAY3,   464L, 499L,  -35,   -7.543103f),
                getDelta(DAY7,   416L, 499L,  -83,  -19.951923f),
                getDelta(DAY14,  332L, 499L, -167,  -50.301205f),
                getDelta(DAY30,  140L, 499L, -359, -256.4286f)
        );
        assertEquals(expected, deltas);
    }

    @Test
    public void testGetReverseDeltasListFullyPopulatedGettingBetter() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        // These values are getting lower over time, which means getting better for reverse deltas
        List<Entry<Instant, Long>> entries = Stream.iterate(1, i -> i + 1).limit(90 * 12)
                .map(i -> new SimpleEntry<>(now.minusSeconds(HOURS.toSeconds(2 * i)), 10L + i))
                .collect(toList());
        List<Delta> deltas = Delta.getReverseDeltas(entries, Entry::getKey, Entry::getValue);
        List<Delta> expected = asList(
                getDelta(HOUR6,   13L, 11L,   2, 15.384616f),
                getDelta(HOUR12,  16L, 11L,   5, 31.25f),
                getDelta(DAY1,    22L, 11L,  11, 50.0f),
                getDelta(DAY3,    46L, 11L,  35, 76.08696f),
                getDelta(DAY7,    94L, 11L,  83, 88.297874f),
                getDelta(DAY14,  178L, 11L, 167, 93.82023f),
                getDelta(DAY30,  370L, 11L, 359, 97.02703f)
        );
        assertEquals(expected, deltas);
    }

    @Test
    public void testGettersAndSetters() {
        Delta delta = new Delta().setInterval(DAY3).setOldest(10L).setNewest(15L).setChange(5).setPercent(5.25f);

        assertEquals(DAY3, delta.getInterval());
        assertEquals(10L, (long) delta.getOldest());
        assertEquals(15L, (long) delta.getNewest());
        assertEquals(5, delta.getChange());
        assertEquals(5.25f, delta.getPercent(), 0.001);
    }

    @Test
    public void testEquals() {
        Delta delta1 = new Delta().setInterval(DAY3).setOldest(10L).setNewest(15L).setChange(5).setPercent(5.25f);
        Delta delta2 = new Delta().setInterval(DAY3).setOldest(10L).setNewest(15L).setChange(5).setPercent(5.25f);
        assertEquals(delta1, delta2);
    }

    @Test
    public void testHashCode() {
        Delta delta = new Delta().setInterval(DAY3).setOldest(10L).setNewest(15L).setChange(5).setPercent(5.25f);
        assertEquals(28629151, new Delta().hashCode());
        assertNotEquals(0, delta.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        Delta delta = new Delta().setInterval(DAY3).setOldest(10L).setNewest(15L).setChange(5).setPercent(5.25f);
        assertEquals("Delta{interval=3d, oldest=10, newest=15, change=5, percent=5.25}", delta.toString());
    }
}
