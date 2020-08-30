package vstocks.db.system;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.BaseServiceImplIT;
import vstocks.db.UserService;
import vstocks.db.UserServiceImpl;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.User;
import vstocks.model.system.TotalUserCount;
import vstocks.model.system.TotalUserCountCollection;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.DatabaseField.COUNT;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class TotalUserCountServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private TotalUserCountService totalUserCountService;

    private final User user1 = new User()
            .setId(generateId("user1@domain.com"))
            .setEmail("user1@domain.com")
            .setUsername("name1")
            .setDisplayName("Name1");
    private final User user2 = new User()
            .setId(generateId("user2@domain.com"))
            .setEmail("user2@domain.com")
            .setUsername("name2")
            .setDisplayName("Name2");

    private final TotalUserCount totalUserCount1 = new TotalUserCount()
            .setTimestamp(now)
            .setCount(1);
    private final TotalUserCount totalUserCount2 = new TotalUserCount()
            .setTimestamp(now.minusSeconds(10))
            .setCount(2);

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        totalUserCountService = new TotalUserCountServiceImpl(dataSourceExternalResource.get());
    }

    @After
    public void cleanup() {
        totalUserCountService.truncate();
        userService.truncate();
    }

    @Test
    public void testGenerateEmpty() {
        assertEquals(1, totalUserCountService.generate());

        Results<TotalUserCount> results = totalUserCountService.getAll(new Page(), emptyList());
        assertEquals(Page.from(1, 20, 1, 1), results.getPage());
        assertEquals(1, results.getResults().size());
        assertEquals(0, results.getResults().iterator().next().getCount());
    }

    @Test
    public void testGenerateSomeUsers() {
        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));

        assertEquals(1, totalUserCountService.generate());

        Results<TotalUserCount> results = totalUserCountService.getAll(new Page(), emptyList());
        assertEquals(Page.from(1, 20, 1, 1), results.getPage());
        assertEquals(1, results.getResults().size());
        assertEquals(2, results.getResults().iterator().next().getCount());
    }

    @Test
    public void testGetLatestNone() {
        TotalUserCountCollection latest = totalUserCountService.getLatest();
        assertTrue(latest.getCounts().isEmpty());
        assertEquals(getZeroDeltas(), latest.getDeltas());
    }

    @Test
    public void testGetLatestSome() {
        assertEquals(1, totalUserCountService.add(totalUserCount1));
        assertEquals(1, totalUserCountService.add(totalUserCount2));

        TotalUserCountCollection latest = totalUserCountService.getLatest();
        validateResults(latest.getCounts(), totalUserCount1, totalUserCount2);
        assertEquals(getDeltas(2L, 1L, -1, -50f), latest.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<TotalUserCount> results = totalUserCountService.getAll(new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, totalUserCountService.add(totalUserCount1));
        assertEquals(1, totalUserCountService.add(totalUserCount2));

        Results<TotalUserCount> results = totalUserCountService.getAll(new Page(), emptyList());
        validateResults(results, totalUserCount1, totalUserCount2);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, totalUserCountService.add(totalUserCount1));
        assertEquals(1, totalUserCountService.add(totalUserCount2));

        List<Sort> sort = asList(COUNT.toSort(DESC), TIMESTAMP.toSort(DESC));
        Results<TotalUserCount> results = totalUserCountService.getAll(new Page(), sort);
        validateResults(results, totalUserCount2, totalUserCount1);
    }

    @Test
    public void testAddConflict() {
        assertEquals(1, totalUserCountService.add(totalUserCount1));
        assertEquals(0, totalUserCountService.add(totalUserCount1));
    }

    @Test
    public void testAgeOff() {
        assertEquals(1, totalUserCountService.add(totalUserCount1));
        assertEquals(1, totalUserCountService.add(totalUserCount2));

        totalUserCountService.ageOff(now.minusSeconds(5));

        Results<TotalUserCount> results = totalUserCountService.getAll(new Page(), emptyList());
        validateResults(results, totalUserCount1);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, totalUserCountService.add(totalUserCount1));
        assertEquals(1, totalUserCountService.add(totalUserCount2));

        totalUserCountService.truncate();

        Results<TotalUserCount> results = totalUserCountService.getAll(new Page(), emptyList());
        validateResults(results);
    }
}
