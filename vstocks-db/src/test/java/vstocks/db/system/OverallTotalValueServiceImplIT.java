package vstocks.db.system;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.User;
import vstocks.model.system.OverallTotalValue;
import vstocks.model.system.OverallTotalValueCollection;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.User.generateId;

public class OverallTotalValueServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private UserCreditsService userCreditsService;
    private OverallTotalValueService overallTotalValueService;

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

    private final OverallTotalValue overallTotalValue1 = new OverallTotalValue()
            .setTimestamp(now)
            .setValue(10);
    private final OverallTotalValue overallTotalValue2 = new OverallTotalValue()
            .setTimestamp(now.minusSeconds(10))
            .setValue(9);

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        userCreditsService = new UserCreditsServiceImpl(dataSourceExternalResource.get());
        overallTotalValueService = new OverallTotalValueServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));
    }

    @After
    public void cleanup() {
        overallTotalValueService.truncate();
        userCreditsService.truncate();
        userService.truncate();
    }

    @Test
    public void testGenerate() {
        assertEquals(1, overallTotalValueService.generate());

        Results<OverallTotalValue> results = overallTotalValueService.getAll(new Page(), emptyList());
        assertEquals(1, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(20000, results.getResults().iterator().next().getValue());
    }

    @Test
    public void testGetLatestNone() {
        OverallTotalValueCollection latest = overallTotalValueService.getLatest();
        assertTrue(latest.getValues().isEmpty());
        assertEquals(getZeroDeltas(), latest.getDeltas());
    }

    @Test
    public void testGetLatestSome() {
        assertEquals(1, overallTotalValueService.add(overallTotalValue1));
        assertEquals(1, overallTotalValueService.add(overallTotalValue2));

        OverallTotalValueCollection latest = overallTotalValueService.getLatest();
        validateResults(latest.getValues(), overallTotalValue1, overallTotalValue2);
        assertEquals(getDeltas(1, 11.111112f), latest.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<OverallTotalValue> results = overallTotalValueService.getAll(new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, overallTotalValueService.add(overallTotalValue1));
        assertEquals(1, overallTotalValueService.add(overallTotalValue2));

        Results<OverallTotalValue> results = overallTotalValueService.getAll(new Page(), emptyList());
        validateResults(results, overallTotalValue1, overallTotalValue2);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, overallTotalValueService.add(overallTotalValue1));
        assertEquals(1, overallTotalValueService.add(overallTotalValue2));

        List<Sort> sort = singletonList(TIMESTAMP.toSort());
        Results<OverallTotalValue> results = overallTotalValueService.getAll(new Page(), sort);
        validateResults(results, overallTotalValue2, overallTotalValue1);
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        assertEquals(1, overallTotalValueService.add(overallTotalValue1));
        overallTotalValueService.add(overallTotalValue1);
    }

    @Test
    public void testAgeOff() {
        assertEquals(1, overallTotalValueService.add(overallTotalValue1));
        assertEquals(1, overallTotalValueService.add(overallTotalValue2));

        assertEquals(1, overallTotalValueService.ageOff(now.minusSeconds(5)));

        Results<OverallTotalValue> results = overallTotalValueService.getAll(new Page(), emptyList());
        validateResults(results, overallTotalValue1);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, overallTotalValueService.add(overallTotalValue1));
        assertEquals(1, overallTotalValueService.add(overallTotalValue2));

        assertEquals(2, overallTotalValueService.truncate());

        Results<OverallTotalValue> results = overallTotalValueService.getAll(new Page(), emptyList());
        validateResults(results);
    }
}
