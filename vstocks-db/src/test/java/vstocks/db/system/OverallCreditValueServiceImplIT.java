package vstocks.db.system;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.User;
import vstocks.model.system.OverallCreditValue;
import vstocks.model.system.OverallCreditValueCollection;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.User.generateId;

public class OverallCreditValueServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private UserCreditsService userCreditsService;
    private OverallCreditValueService overallCreditValueService;

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

    private final OverallCreditValue overallCreditValue1 = new OverallCreditValue()
            .setTimestamp(now)
            .setValue(10);
    private final OverallCreditValue overallCreditValue2 = new OverallCreditValue()
            .setTimestamp(now.minusSeconds(10))
            .setValue(9);

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        userCreditsService = new UserCreditsServiceImpl(dataSourceExternalResource.get());
        overallCreditValueService = new OverallCreditValueServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));
    }

    @After
    public void cleanup() {
        overallCreditValueService.truncate();
        userCreditsService.truncate();
        userService.truncate();
    }

    @Test
    public void testGenerate() {
        assertEquals(1, overallCreditValueService.generate());

        Results<OverallCreditValue> results = overallCreditValueService.getAll(new Page(), emptyList());
        assertEquals(Page.from(1, 20, 1, 1), results.getPage());
        assertEquals(1, results.getResults().size());
        assertEquals(20000, results.getResults().iterator().next().getValue());
    }

    @Test
    public void testGetLatestNone() {
        OverallCreditValueCollection latest = overallCreditValueService.getLatest();
        assertTrue(latest.getValues().isEmpty());
        assertEquals(getZeroDeltas(), latest.getDeltas());
    }

    @Test
    public void testGetLatestSome() {
        assertEquals(1, overallCreditValueService.add(overallCreditValue1));
        assertEquals(1, overallCreditValueService.add(overallCreditValue2));

        OverallCreditValueCollection latest = overallCreditValueService.getLatest();
        validateResults(latest.getValues(), overallCreditValue1, overallCreditValue2);
        assertEquals(getDeltas(9L, 10L, 1, 11.111112f), latest.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<OverallCreditValue> results = overallCreditValueService.getAll(new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, overallCreditValueService.add(overallCreditValue1));
        assertEquals(1, overallCreditValueService.add(overallCreditValue2));

        Results<OverallCreditValue> results = overallCreditValueService.getAll(new Page(), emptyList());
        validateResults(results, overallCreditValue1, overallCreditValue2);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, overallCreditValueService.add(overallCreditValue1));
        assertEquals(1, overallCreditValueService.add(overallCreditValue2));

        List<Sort> sort = singletonList(TIMESTAMP.toSort());
        Results<OverallCreditValue> results = overallCreditValueService.getAll(new Page(), sort);
        validateResults(results, overallCreditValue2, overallCreditValue1);
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        assertEquals(1, overallCreditValueService.add(overallCreditValue1));
        overallCreditValueService.add(overallCreditValue1);
    }

    @Test
    public void testAgeOff() {
        assertEquals(1, overallCreditValueService.add(overallCreditValue1));
        assertEquals(1, overallCreditValueService.add(overallCreditValue2));

        assertEquals(1, overallCreditValueService.ageOff(now.minusSeconds(5)));

        Results<OverallCreditValue> results = overallCreditValueService.getAll(new Page(), emptyList());
        validateResults(results, overallCreditValue1);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, overallCreditValueService.add(overallCreditValue1));
        assertEquals(1, overallCreditValueService.add(overallCreditValue2));

        assertEquals(2, overallCreditValueService.truncate());

        Results<OverallCreditValue> results = overallCreditValueService.getAll(new Page(), emptyList());
        validateResults(results);
    }
}
