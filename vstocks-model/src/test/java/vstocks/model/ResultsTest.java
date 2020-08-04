package vstocks.model;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class ResultsTest {
    @Test
    public void testGettersAndSetters() {
        Results<User> results = new Results<User>()
                .setPage(new Page().setPage(5).setSize(30))
                .setTotal(10)
                .setResults(asList(new User().setId("1"), new User().setId("2")));

        assertEquals(5, results.getPage().getPage());
        assertEquals(30, results.getPage().getSize());
        assertEquals(10, results.getTotal());
        assertEquals(2, results.getResults().size());
    }

    @Test
    public void testEquals() {
        Results<User> results1 = new Results<User>()
                .setPage(new Page().setPage(5).setSize(30))
                .setTotal(10)
                .setResults(asList(new User().setId("1"), new User().setId("2")));
        Results<User> results2 = new Results<User>()
                .setPage(new Page().setPage(5).setSize(30))
                .setTotal(10)
                .setResults(asList(new User().setId("1"), new User().setId("2")));
        assertEquals(results1, results2);
    }

    @Test
    public void testHashCode() {
        Results<User> results = new Results<User>()
                .setPage(new Page().setPage(5).setSize(30))
                .setTotal(10)
                .setResults(asList(new User().setId("1"), new User().setId("2")));
        assertEquals(1134929, results.hashCode());
    }

    @Test
    public void testToString() {
        Results<User> results = new Results<User>()
                .setPage(new Page().setPage(5).setSize(30))
                .setTotal(10)
                .setResults(asList(new User().setId("1"), new User().setId("2")));
        assertEquals("Results{page=Page{page=5, size=30}, total=10, results=[User{id='1', username='null', "
                + "source=null, displayName='null'}, User{id='2', username='null', source=null, displayName='null'}]}",
                results.toString());
    }
}
