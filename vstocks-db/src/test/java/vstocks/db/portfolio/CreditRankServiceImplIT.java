package vstocks.db.portfolio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.User;
import vstocks.model.portfolio.CreditRank;
import vstocks.model.portfolio.CreditRankCollection;
import vstocks.model.portfolio.RankedUser;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
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
            .setBatch(2)
            .setUserId(user1.getId())
            .setTimestamp(now)
            .setRank(1)
            .setValue(10);
    private final CreditRank creditRank12 = new CreditRank()
            .setBatch(1)
            .setUserId(user1.getId())
            .setTimestamp(now.minusSeconds(10))
            .setRank(2)
            .setValue(9);
    private final CreditRank creditRank21 = new CreditRank()
            .setBatch(2)
            .setUserId(user2.getId())
            .setTimestamp(now)
            .setRank(2)
            .setValue(10);
    private final CreditRank creditRank22 = new CreditRank()
            .setBatch(1)
            .setUserId(user2.getId())
            .setTimestamp(now.minusSeconds(10))
            .setRank(3)
            .setValue(9);

    private final RankedUser rankedUser1 = new RankedUser()
            .setUser(user1)
            .setBatch(creditRank11.getBatch())
            .setTimestamp(creditRank11.getTimestamp())
            .setRank(creditRank11.getRank())
            .setValue(creditRank11.getValue());
    private final RankedUser rankedUser2 = new RankedUser()
            .setUser(user2)
            .setBatch(creditRank21.getBatch())
            .setTimestamp(creditRank21.getTimestamp())
            .setRank(creditRank21.getRank())
            .setValue(creditRank21.getValue());

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

        Results<CreditRank> results = creditRankService.getAll(new Page(), emptyList());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().stream().map(CreditRank::getRank).allMatch(rank -> rank == 1));
        assertTrue(results.getResults().stream().map(CreditRank::getValue).allMatch(value -> value == 10000));
        assertEquals("1,1", results.getResults().stream().map(r -> "" + r.getRank()).collect(joining(",")));
        assertEquals("10000,10000", results.getResults().stream().map(r -> "" + r.getValue()).collect(joining(",")));
    }

    @Test
    public void testGenerateNoTie() {
        userCreditsService.update(user1.getId(), 10);

        assertEquals(2, creditRankService.generate());

        Results<CreditRank> results = creditRankService.getAll(new Page(), emptyList());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals("1,2", results.getResults().stream().map(r -> "" + r.getRank()).collect(joining(",")));
        assertEquals("10010,10000", results.getResults().stream().map(r -> "" + r.getValue()).collect(joining(",")));
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
        Results<CreditRank> results = creditRankService.getAll(new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, creditRankService.add(creditRank11));
        assertEquals(1, creditRankService.add(creditRank12));

        Results<CreditRank> results = creditRankService.getAll(new Page(), emptyList());
        validateResults(results, creditRank11, creditRank12);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, creditRankService.add(creditRank11));
        assertEquals(1, creditRankService.add(creditRank12));
        assertEquals(1, creditRankService.add(creditRank21));
        assertEquals(1, creditRankService.add(creditRank22));

        List<Sort> sort = asList(RANK.toSort(DESC), USER_ID.toSort());
        Results<CreditRank> results = creditRankService.getAll(new Page(), sort);
        validateResults(results, creditRank22, creditRank12, creditRank21, creditRank11);
    }

    @Test
    public void testGetUsersNone() {
        Results<RankedUser> results = creditRankService.getUsers(new Page());
        validateResults(results);
    }

    @Test
    public void testGetUsersSome() {
        assertEquals(1, creditRankService.add(creditRank11));
        assertEquals(1, creditRankService.add(creditRank12));
        assertEquals(1, creditRankService.add(creditRank21));
        assertEquals(1, creditRankService.add(creditRank22));

        creditRankService.setCurrentBatch(2);
        Results<RankedUser> results = creditRankService.getUsers(new Page());
        validateResults(results, rankedUser1, rankedUser2);
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

        Results<CreditRank> results = creditRankService.getAll(new Page(), emptyList());
        validateResults(results, creditRank11, creditRank21);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, creditRankService.add(creditRank11));
        assertEquals(1, creditRankService.add(creditRank12));
        assertEquals(1, creditRankService.add(creditRank21));
        assertEquals(1, creditRankService.add(creditRank22));

        creditRankService.truncate();

        Results<CreditRank> results = creditRankService.getAll(new Page(), emptyList());
        validateResults(results);
    }
}
