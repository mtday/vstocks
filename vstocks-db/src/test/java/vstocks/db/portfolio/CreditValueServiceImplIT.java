package vstocks.db.portfolio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.*;
import vstocks.model.portfolio.CreditValue;
import vstocks.model.portfolio.CreditValueCollection;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.DatabaseField.VALUE;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class CreditValueServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private UserCreditsService userCreditsService;
    private CreditValueService creditValueService;

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

    private final CreditValue creditValue11 = new CreditValue()
            .setUserId(user1.getId())
            .setTimestamp(now)
            .setValue(11);
    private final CreditValue creditValue12 = new CreditValue()
            .setUserId(user1.getId())
            .setTimestamp(now.minusSeconds(10))
            .setValue(12);
    private final CreditValue creditValue21 = new CreditValue()
            .setUserId(user2.getId())
            .setTimestamp(now)
            .setValue(21);
    private final CreditValue creditValue22 = new CreditValue()
            .setUserId(user2.getId())
            .setTimestamp(now.minusSeconds(10))
            .setValue(22);

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        userCreditsService = new UserCreditsServiceImpl(dataSourceExternalResource.get());
        creditValueService = new CreditValueServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));
    }

    @After
    public void cleanup() {
        creditValueService.truncate();
        userCreditsService.truncate();
        userService.truncate();
    }

    @Test
    public void testGenerateTie() {
        assertEquals(2, creditValueService.generate());

        Results<CreditValue> results = creditValueService.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().stream().map(CreditValue::getValue).allMatch(value -> value == 10000));
    }

    @Test
    public void testGenerateNoTie() {
        userCreditsService.update(user1.getId(), 10);

        assertEquals(2, creditValueService.generate());

        Results<CreditValue> results = creditValueService.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(20010, results.getResults().stream().mapToLong(CreditValue::getValue).sum());
    }

    @Test
    public void testGetLatestNone() {
        CreditValueCollection latest = creditValueService.getLatest(user1.getId());
        assertTrue(latest.getValues().isEmpty());
        assertEquals(getZeroDeltas(), latest.getDeltas());
    }

    @Test
    public void testGetLatestSome() {
        assertEquals(1, creditValueService.add(creditValue11));
        assertEquals(1, creditValueService.add(creditValue12));

        CreditValueCollection latest = creditValueService.getLatest(user1.getId());
        validateResults(latest.getValues(), creditValue11, creditValue12);
        assertEquals(getDeltas(-1, -8.333334f), latest.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<CreditValue> results = creditValueService.getAll(new Page(), emptySet());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, creditValueService.add(creditValue11));
        assertEquals(1, creditValueService.add(creditValue12));

        Results<CreditValue> results = creditValueService.getAll(new Page(), emptySet());
        validateResults(results, creditValue12, creditValue11);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, creditValueService.add(creditValue11));
        assertEquals(1, creditValueService.add(creditValue12));
        assertEquals(1, creditValueService.add(creditValue21));
        assertEquals(1, creditValueService.add(creditValue22));

        Set<Sort> sort = new LinkedHashSet<>(asList(VALUE.toSort(DESC), USER_ID.toSort()));
        Results<CreditValue> results = creditValueService.getAll(new Page(), sort);
        validateResults(results, creditValue22, creditValue21, creditValue12, creditValue11);
    }

    @Test
    public void testAddConflict() {
        assertEquals(1, creditValueService.add(creditValue11));
        assertEquals(0, creditValueService.add(creditValue11));
    }

    @Test
    public void testAgeOff() {
        assertEquals(1, creditValueService.add(creditValue11));
        assertEquals(1, creditValueService.add(creditValue12));
        assertEquals(1, creditValueService.add(creditValue21));
        assertEquals(1, creditValueService.add(creditValue22));

        creditValueService.ageOff(now.minusSeconds(5));

        Results<CreditValue> results = creditValueService.getAll(new Page(), emptySet());
        validateResults(results, creditValue21, creditValue11);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, creditValueService.add(creditValue11));
        assertEquals(1, creditValueService.add(creditValue12));
        assertEquals(1, creditValueService.add(creditValue21));
        assertEquals(1, creditValueService.add(creditValue22));

        creditValueService.truncate();

        Results<CreditValue> results = creditValueService.getAll(new Page(), emptySet());
        validateResults(results);
    }
}
