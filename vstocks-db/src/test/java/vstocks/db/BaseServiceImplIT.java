package vstocks.db;

import org.junit.ClassRule;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;
import vstocks.model.Results;

import java.time.Instant;
import java.util.*;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public abstract class BaseServiceImplIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    public final Instant now = Instant.now().truncatedTo(SECONDS);

    @SuppressWarnings("all")
    @SafeVarargs
    public final <T> void validateResults(Results<T> results, T... expected) {
        List<T> expectedList = new ArrayList<>();
        for (T t : expected) {
            expectedList.add(t);
        }

        assertEquals(expectedList.size(), results.getTotal());
        assertEquals(expectedList.size(), results.getResults().size());

        Iterator<T> expectedIter = expectedList.iterator();
        Iterator<T> actualIter = results.getResults().iterator();
        int index = 0;
        while (expectedIter.hasNext() && actualIter.hasNext()) {
            assertEquals("Failed on number " + ++index, expectedIter.next(), actualIter.next());
        }
    }

    @SuppressWarnings("all")
    @SafeVarargs
    public final <T> void validateResults(List<T> results, T... expected) {
        List<T> expectedList = new ArrayList<>();
        for (T t : expected) {
            expectedList.add(t);
        }

        assertEquals(expectedList.size(), results.size());

        Iterator<T> expectedIter = expectedList.iterator();
        Iterator<T> actualIter = results.iterator();
        while (expectedIter.hasNext() && actualIter.hasNext()) {
            assertEquals(expectedIter.next(), actualIter.next());
        }
    }

    public Map<DeltaInterval, Delta> getZeroDeltas() {
        return getDeltas(0, 0f);
    }

    public Map<DeltaInterval, Delta> getDeltas(long change, float percent) {
        Map<DeltaInterval, Delta> deltas = new TreeMap<>();
        Arrays.stream(DeltaInterval.values())
                .map(interval -> new Delta().setInterval(interval).setChange(change).setPercent(percent))
                .forEach(delta -> deltas.put(delta.getInterval(), delta));
        return deltas;
    }
}
