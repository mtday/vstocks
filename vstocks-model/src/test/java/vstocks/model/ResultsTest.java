package vstocks.model;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class ResultsTest {
    @Test
    public void testGettersAndSetters() {
        Page page = new Page()
                .setPage(3)
                .setSize(10)
                .setTotalPages(5)
                .setFirstRow(21)
                .setLastRow(30)
                .setTotalRows(46);
        Results<String> results = new Results<String>().setPage(page).setResults(asList("1", "2"));

        assertEquals(page, results.getPage());
        assertEquals(asList("1", "2"), results.getResults());
    }

    @Test
    public void testEquals() {
        Page page = new Page()
                .setPage(3)
                .setSize(10)
                .setTotalPages(5)
                .setFirstRow(21)
                .setLastRow(30)
                .setTotalRows(46);
        Results<String> results1 = new Results<String>().setPage(page).setResults(asList("1", "2"));
        Results<String> results2 = new Results<String>().setPage(page).setResults(asList("1", "2"));
        assertEquals(results1, results2);
    }

    @Test
    public void testHashCode() {
        Page page = new Page()
                .setPage(3)
                .setSize(10)
                .setTotalPages(5)
                .setFirstRow(21)
                .setLastRow(30)
                .setTotalRows(46);
        Results<String> results = new Results<String>().setPage(page).setResults(asList("1", "2"));
        assertEquals(401922555, results.hashCode());
    }

    @Test
    public void testToString() {
        Page page = new Page()
                .setPage(3)
                .setSize(10)
                .setTotalPages(5)
                .setFirstRow(21)
                .setLastRow(30)
                .setTotalRows(46);
        Results<String> results = new Results<String>().setPage(page).setResults(asList("1", "2"));
        assertEquals("Results{page=" + page + ", results=[1, 2]}", results.toString());
    }
}
