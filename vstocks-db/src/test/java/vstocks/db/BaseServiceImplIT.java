package vstocks.db;

import org.junit.ClassRule;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;
import vstocks.model.Results;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public abstract class BaseServiceImplIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    public final Instant now = Instant.now().truncatedTo(SECONDS);

    @SuppressWarnings("all")
    @SafeVarargs
    public final <T> void validateResults(Results<T> results, int total, int page, T... expected) {
        List<T> expectedList = new ArrayList<>();
        for (T t : expected) {
            expectedList.add(t);
        }

        assertEquals(total, results.getTotal());
        assertEquals(page, results.getPage().getPage());
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

    public List<Delta> getZeroDeltas() {
        return getDeltas(null, null, 0, 0f);
    }

    public List<Delta> getDeltas(Long oldest, Long newest, long change, float percent) {
        return Arrays.stream(DeltaInterval.values())
                .map(interval -> new Delta()
                        .setInterval(interval)
                        .setOldest(oldest)
                        .setNewest(newest)
                        .setChange(change)
                        .setPercent(percent))
                .collect(toList());
    }
}
