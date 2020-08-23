package vstocks.db.portfolio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.*;
import vstocks.model.portfolio.CreditRank;
import vstocks.model.portfolio.CreditRankCollection;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.DatabaseField.RANK;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class CreditRankServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private UserCreditsService userCreditsService;
    private CreditRankService creditRankService;

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

    private final CreditRank creditRank11 = new CreditRank()
            .setUserId(user1.getId())
            .setTimestamp(now)
            .setRank(1);
    private final CreditRank creditRank12 = new CreditRank()
            .setUserId(user1.getId())
            .setTimestamp(now.minusSeconds(10))
            .setRank(2);
    private final CreditRank creditRank21 = new CreditRank()
            .setUserId(user2.getId())
            .setTimestamp(now)
            .setRank(2);
    private final CreditRank creditRank22 = new CreditRank()
            .setUserId(user2.getId())
            .setTimestamp(now.minusSeconds(10))
            .setRank(3);

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        userCreditsService = new UserCreditsServiceImpl(dataSourceExternalResource.get());
        creditRankService = new CreditRankServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));
    }

    @After
    public void cleanup() {
        creditRankService.truncate();
        userCreditsService.truncate();
        userService.truncate();
    }

    @Test
    public void testGenerateTie() {
        assertEquals(2, creditRankService.generate());

        Results<CreditRank> results = creditRankService.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().stream().map(CreditRank::getRank).allMatch(rank -> rank == 1));
    }

    @Test
    public void testGenerateNoTie() {
        userCreditsService.update(user1.getId(), 10);

        assertEquals(2, creditRankService.generate());

        Results<CreditRank> results = creditRankService.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(3, results.getResults().stream().mapToLong(CreditRank::getRank).sum());
    }

    @Test
    public void testGetLatestNone() {
        CreditRankCollection latest = creditRankService.getLatest(user1.getId());
        assertTrue(latest.getRanks().isEmpty());
        assertEquals(getZeroDeltas(), latest.getDeltas());
    }

    @Test
    public void testGetLatestSome() {
        assertEquals(1, creditRankService.add(creditRank11));
        assertEquals(1, creditRankService.add(creditRank12));

        CreditRankCollection latest = creditRankService.getLatest(user1.getId());
        validateResults(latest.getRanks(), creditRank11, creditRank12);
        assertEquals(getDeltas(-1, -50f), latest.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<CreditRank> results = creditRankService.getAll(new Page(), emptySet());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, creditRankService.add(creditRank11));
        assertEquals(1, creditRankService.add(creditRank12));

        Results<CreditRank> results = creditRankService.getAll(new Page(), emptySet());
        validateResults(results, creditRank11, creditRank12);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, creditRankService.add(creditRank11));
        assertEquals(1, creditRankService.add(creditRank12));
        assertEquals(1, creditRankService.add(creditRank21));
        assertEquals(1, creditRankService.add(creditRank22));

        Set<Sort> sort = new LinkedHashSet<>(asList(RANK.toSort(DESC), USER_ID.toSort()));
        Results<CreditRank> results = creditRankService.getAll(new Page(), sort);
        validateResults(results, creditRank22, creditRank12, creditRank21, creditRank11);
    }

    @Test
    public void testAddConflict() {
        assertEquals(1, creditRankService.add(creditRank11));
        assertEquals(0, creditRankService.add(creditRank11));
    }

    @Test
    public void testAgeOff() {
        assertEquals(1, creditRankService.add(creditRank11));
        assertEquals(1, creditRankService.add(creditRank12));
        assertEquals(1, creditRankService.add(creditRank21));
        assertEquals(1, creditRankService.add(creditRank22));

        creditRankService.ageOff(now.minusSeconds(5));

        Results<CreditRank> results = creditRankService.getAll(new Page(), emptySet());
        validateResults(results, creditRank11, creditRank21);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, creditRankService.add(creditRank11));
        assertEquals(1, creditRankService.add(creditRank12));
        assertEquals(1, creditRankService.add(creditRank21));
        assertEquals(1, creditRankService.add(creditRank22));

        creditRankService.truncate();

        Results<CreditRank> results = creditRankService.getAll(new Page(), emptySet());
        validateResults(results);
    }
}
