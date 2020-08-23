package vstocks.db.portfolio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.User;
import vstocks.model.portfolio.TotalValue;
import vstocks.model.portfolio.TotalValueCollection;
import vstocks.model.portfolio.ValuedUser;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.DatabaseField.VALUE;
import static vstocks.model.User.generateId;

public class TotalValueServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private UserCreditsService userCreditsService;
    private TotalValueService totalValueService;

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

    private final TotalValue totalValue11 = new TotalValue()
            .setBatch(2)
            .setUserId(user1.getId())
            .setTimestamp(now)
            .setValue(11);
    private final TotalValue totalValue12 = new TotalValue()
            .setBatch(1)
            .setUserId(user1.getId())
            .setTimestamp(now.minusSeconds(10))
            .setValue(12);
    private final TotalValue totalValue21 = new TotalValue()
            .setBatch(2)
            .setUserId(user2.getId())
            .setTimestamp(now)
            .setValue(21);
    private final TotalValue totalValue22 = new TotalValue()
            .setBatch(1)
            .setUserId(user2.getId())
            .setTimestamp(now.minusSeconds(10))
            .setValue(22);

    private final ValuedUser valuedUser1 = new ValuedUser()
            .setUser(user1)
            .setBatch(totalValue11.getBatch())
            .setTimestamp(totalValue11.getTimestamp())
            .setValue(totalValue11.getValue());
    private final ValuedUser valuedUser2 = new ValuedUser()
            .setUser(user2)
            .setBatch(totalValue21.getBatch())
            .setTimestamp(totalValue21.getTimestamp())
            .setValue(totalValue21.getValue());

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        userCreditsService = new UserCreditsServiceImpl(dataSourceExternalResource.get());
        totalValueService = new TotalValueServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));
    }

    @After
    public void cleanup() {
        totalValueService.truncate();
        userCreditsService.truncate();
        userService.truncate();
    }

    @Test
    public void testGenerateTie() {
        assertEquals(2, totalValueService.generate());

        Results<TotalValue> results = totalValueService.getAll(new Page(), emptyList());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().stream().map(TotalValue::getValue).allMatch(value -> value == 10000));
    }

    @Test
    public void testGenerateNoTie() {
        userCreditsService.update(user1.getId(), 10);

        assertEquals(2, totalValueService.generate());

        Results<TotalValue> results = totalValueService.getAll(new Page(), emptyList());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(20010, results.getResults().stream().mapToLong(TotalValue::getValue).sum());
    }

    @Test
    public void testGetLatestNone() {
        TotalValueCollection latest = totalValueService.getLatest(user1.getId());
        assertTrue(latest.getValues().isEmpty());
        assertEquals(getZeroDeltas(), latest.getDeltas());
    }

    @Test
    public void testGetLatestSome() {
        assertEquals(1, totalValueService.add(totalValue11));
        assertEquals(1, totalValueService.add(totalValue12));

        TotalValueCollection latest = totalValueService.getLatest(user1.getId());
        validateResults(latest.getValues(), totalValue11, totalValue12);
        assertEquals(getDeltas(-1, -8.333334f), latest.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<TotalValue> results = totalValueService.getAll(new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, totalValueService.add(totalValue11));
        assertEquals(1, totalValueService.add(totalValue12));

        Results<TotalValue> results = totalValueService.getAll(new Page(), emptyList());
        validateResults(results, totalValue11, totalValue12);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, totalValueService.add(totalValue11));
        assertEquals(1, totalValueService.add(totalValue12));
        assertEquals(1, totalValueService.add(totalValue21));
        assertEquals(1, totalValueService.add(totalValue22));

        List<Sort> sort = asList(VALUE.toSort(), USER_ID.toSort());
        Results<TotalValue> results = totalValueService.getAll(new Page(), sort);
        validateResults(results, totalValue11, totalValue12, totalValue21, totalValue22);
    }

    @Test
    public void testGetUsersNone() {
        Results<ValuedUser> results = totalValueService.getUsers(new Page());
        validateResults(results);
    }

    @Test
    public void testGetUsersSome() {
        assertEquals(1, totalValueService.add(totalValue11));
        assertEquals(1, totalValueService.add(totalValue12));
        assertEquals(1, totalValueService.add(totalValue21));
        assertEquals(1, totalValueService.add(totalValue22));

        totalValueService.setCurrentBatch(2);
        Results<ValuedUser> results = totalValueService.getUsers(new Page());
        validateResults(results, valuedUser2, valuedUser1);
    }

    @Test
    public void testAddConflict() {
        assertEquals(1, totalValueService.add(totalValue11));
        assertEquals(0, totalValueService.add(totalValue11));
    }

    @Test
    public void testAgeOff() {
        assertEquals(1, totalValueService.add(totalValue11));
        assertEquals(1, totalValueService.add(totalValue12));
        assertEquals(1, totalValueService.add(totalValue21));
        assertEquals(1, totalValueService.add(totalValue22));

        totalValueService.ageOff(now.minusSeconds(5));

        Results<TotalValue> results = totalValueService.getAll(new Page(), emptyList());
        validateResults(results, totalValue21, totalValue11);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, totalValueService.add(totalValue11));
        assertEquals(1, totalValueService.add(totalValue12));
        assertEquals(1, totalValueService.add(totalValue21));
        assertEquals(1, totalValueService.add(totalValue22));

        totalValueService.truncate();

        Results<TotalValue> results = totalValueService.getAll(new Page(), emptyList());
        validateResults(results);
    }
}
