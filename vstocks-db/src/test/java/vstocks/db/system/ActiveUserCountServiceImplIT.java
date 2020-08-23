package vstocks.db.system;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.*;
import vstocks.model.system.ActiveUserCount;
import vstocks.model.system.ActiveUserCountCollection;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.ActivityType.USER_LOGIN;
import static vstocks.model.DatabaseField.COUNT;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class ActiveUserCountServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private ActivityLogService activityLogService;
    private ActiveUserCountService activeUserCountService;

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

    private final ActivityLog activityLog1 = new ActivityLog()
            .setId("id1")
            .setUserId(user1.getId())
            .setType(USER_LOGIN)
            .setTimestamp(now);
    private final ActivityLog activityLog2 = new ActivityLog()
            .setId("id2")
            .setUserId(user2.getId())
            .setType(USER_LOGIN)
            .setTimestamp(now);

    private final ActiveUserCount activeUserCount1 = new ActiveUserCount()
            .setTimestamp(now)
            .setCount(1);
    private final ActiveUserCount activeUserCount2 = new ActiveUserCount()
            .setTimestamp(now.minusSeconds(10))
            .setCount(2);

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        activityLogService = new ActivityLogServiceImpl(dataSourceExternalResource.get());
        activeUserCountService = new ActiveUserCountServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));
    }

    @After
    public void cleanup() {
        activeUserCountService.truncate();
        activityLogService.truncate();
        userService.truncate();
    }

    @Test
    public void testGenerateEmpty() {
        assertEquals(1, activeUserCountService.generate());

        Results<ActiveUserCount> results = activeUserCountService.getAll(new Page(), emptyList());
        assertEquals(1, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(0, results.getResults().iterator().next().getCount());
    }

    @Test
    public void testGenerateSomeActivity() {
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        assertEquals(1, activeUserCountService.generate());

        Results<ActiveUserCount> results = activeUserCountService.getAll(new Page(), emptyList());
        assertEquals(1, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(2, results.getResults().iterator().next().getCount());
    }

    @Test
    public void testGetLatestNone() {
        ActiveUserCountCollection latest = activeUserCountService.getLatest();
        assertTrue(latest.getCounts().isEmpty());
        assertEquals(getZeroDeltas(), latest.getDeltas());
    }

    @Test
    public void testGetLatestSome() {
        assertEquals(1, activeUserCountService.add(activeUserCount1));
        assertEquals(1, activeUserCountService.add(activeUserCount2));

        ActiveUserCountCollection latest = activeUserCountService.getLatest();
        validateResults(latest.getCounts(), activeUserCount1, activeUserCount2);
        assertEquals(getDeltas(-1, -50f), latest.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<ActiveUserCount> results = activeUserCountService.getAll(new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, activeUserCountService.add(activeUserCount1));
        assertEquals(1, activeUserCountService.add(activeUserCount2));

        Results<ActiveUserCount> results = activeUserCountService.getAll(new Page(), emptyList());
        validateResults(results, activeUserCount1, activeUserCount2);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, activeUserCountService.add(activeUserCount1));
        assertEquals(1, activeUserCountService.add(activeUserCount2));

        List<Sort> sort = asList(COUNT.toSort(DESC), TIMESTAMP.toSort(DESC));
        Results<ActiveUserCount> results = activeUserCountService.getAll(new Page(), sort);
        validateResults(results, activeUserCount2, activeUserCount1);
    }

    @Test
    public void testAddConflict() {
        assertEquals(1, activeUserCountService.add(activeUserCount1));
        assertEquals(0, activeUserCountService.add(activeUserCount1));
    }

    @Test
    public void testAgeOff() {
        assertEquals(1, activeUserCountService.add(activeUserCount1));
        assertEquals(1, activeUserCountService.add(activeUserCount2));

        activeUserCountService.ageOff(now.minusSeconds(5));

        Results<ActiveUserCount> results = activeUserCountService.getAll(new Page(), emptyList());
        validateResults(results, activeUserCount1);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, activeUserCountService.add(activeUserCount1));
        assertEquals(1, activeUserCountService.add(activeUserCount2));

        activeUserCountService.truncate();

        Results<ActiveUserCount> results = activeUserCountService.getAll(new Page(), emptyList());
        validateResults(results);
    }
}
