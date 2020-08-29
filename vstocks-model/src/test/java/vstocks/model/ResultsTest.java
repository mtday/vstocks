package vstocks.model;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class ResultsTest {
    @Test
    public void testGettersAndSetters() {
        Results<String> results = new Results<String>()
                .setPage(new Page().setPage(5).setSize(30))
                .setTotal(10)
                .setResults(asList("1", "2"));

        assertEquals(5, results.getPage().getPage());
        assertEquals(30, results.getPage().getSize());
        assertEquals(10, results.getTotal());
        assertEquals(2, results.getResults().size());
    }

    @Test
    public void testEquals() {
        Results<String> results1 = new Results<String>()
                .setPage(new Page().setPage(5).setSize(30))
                .setTotal(10)
                .setResults(asList("1", "2"));
        Results<String> results2 = new Results<String>()
                .setPage(new Page().setPage(5).setSize(30))
                .setTotal(10)
                .setResults(asList("1", "2"));
        assertEquals(results1, results2);
    }

    @Test
    public void testHashCode() {
        Results<String> results = new Results<String>()
                .setPage(new Page().setPage(5).setSize(30))
                .setTotal(10)
                .setResults(asList("1", "2"));
        assertEquals(1133937, results.hashCode());
    }

    @Test
    public void testToString() {
        Results<String> results = new Results<String>()
                .setPage(new Page().setPage(5).setSize(30))
                .setTotal(10)
                .setResults(asList("1", "2"));
        assertEquals("Results{page=Page{page=5, size=30}, total=10, results=[1, 2]}", results.toString());
    }
}
