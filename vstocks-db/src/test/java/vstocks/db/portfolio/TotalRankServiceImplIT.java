package vstocks.db.portfolio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.User;
import vstocks.model.portfolio.*;

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

public class TotalRankServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private UserCreditsService userCreditsService;
    private TotalRankService totalRankService;

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

    private final TotalRank totalRank11 = new TotalRank()
            .setBatch(2)
            .setUserId(user1.getId())
            .setTimestamp(now)
            .setRank(1)
            .setValue(10);
    private final TotalRank totalRank12 = new TotalRank()
            .setBatch(1)
            .setUserId(user1.getId())
            .setTimestamp(now.minusSeconds(10))
            .setRank(2)
            .setValue(9);
    private final TotalRank totalRank21 = new TotalRank()
            .setBatch(2)
            .setUserId(user2.getId())
            .setTimestamp(now)
            .setRank(2)
            .setValue(10);
    private final TotalRank totalRank22 = new TotalRank()
            .setBatch(1)
            .setUserId(user2.getId())
            .setTimestamp(now.minusSeconds(10))
            .setRank(3)
            .setValue(9);

    private final RankedUser rankedUser1 = new RankedUser()
            .setUser(user1)
            .setBatch(totalRank11.getBatch())
            .setTimestamp(totalRank11.getTimestamp())
            .setRank(totalRank11.getRank())
            .setValue(totalRank11.getValue());
    private final RankedUser rankedUser2 = new RankedUser()
            .setUser(user2)
            .setBatch(totalRank21.getBatch())
            .setTimestamp(totalRank21.getTimestamp())
            .setRank(totalRank21.getRank())
            .setValue(totalRank21.getValue());

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        userCreditsService = new UserCreditsServiceImpl(dataSourceExternalResource.get());
        totalRankService = new TotalRankServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));
    }

    @After
    public void cleanup() {
        totalRankService.truncate();
        userCreditsService.truncate();
        userService.truncate();
    }

    @Test
    public void testGenerateTie() {
        assertEquals(2, totalRankService.generate());

        Results<TotalRank> results = totalRankService.getAll(new Page(), emptyList());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals("1,1", results.getResults().stream().map(r -> "" + r.getRank()).collect(joining(",")));
        assertEquals("10000,10000", results.getResults().stream().map(r -> "" + r.getValue()).collect(joining(",")));
    }

    @Test
    public void testGenerateNoTie() {
        userCreditsService.update(user1.getId(), 10);

        assertEquals(2, totalRankService.generate());

        Results<TotalRank> results = totalRankService.getAll(new Page(), emptyList());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals("1,2", results.getResults().stream().map(r -> "" + r.getRank()).collect(joining(",")));
        assertEquals("10010,10000", results.getResults().stream().map(r -> "" + r.getValue()).collect(joining(",")));
    }

    @Test
    public void testGetLatestNone() {
        TotalRankCollection latest = totalRankService.getLatest(user1.getId());
        assertTrue(latest.getRanks().isEmpty());
        assertEquals(getZeroDeltas(), latest.getDeltas());
    }

    @Test
    public void testGetLatestSome() {
        assertEquals(1, totalRankService.add(totalRank11));
        assertEquals(1, totalRankService.add(totalRank12));

        TotalRankCollection latest = totalRankService.getLatest(user1.getId());
        assertEquals(3, latest.getRanks().size());
        assertEquals(totalRank11, latest.getRanks().get(1));
        assertEquals(totalRank12, latest.getRanks().get(2));

        // This is the derived latest rank
        TotalRank derived = latest.getRanks().get(0);
        assertEquals(10_000, derived.getValue());

        assertEquals(getDeltas(2L, 1L, 1, 50f), latest.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<TotalRank> results = totalRankService.getAll(new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, totalRankService.add(totalRank11));
        assertEquals(1, totalRankService.add(totalRank12));

        Results<TotalRank> results = totalRankService.getAll(new Page(), emptyList());
        validateResults(results, totalRank11, totalRank12);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, totalRankService.add(totalRank11));
        assertEquals(1, totalRankService.add(totalRank12));
        assertEquals(1, totalRankService.add(totalRank21));
        assertEquals(1, totalRankService.add(totalRank22));

        List<Sort> sort = asList(RANK.toSort(DESC), USER_ID.toSort());
        Results<TotalRank> results = totalRankService.getAll(new Page(), sort);
        validateResults(results, totalRank22, totalRank12, totalRank21, totalRank11);
    }

    @Test
    public void testGetUsersNone() {
        Results<RankedUser> results = totalRankService.getUsers(new Page());
        validateResults(results);
    }

    @Test
    public void testGetUsersSome() {
        assertEquals(1, totalRankService.add(totalRank11));
        assertEquals(1, totalRankService.add(totalRank12));
        assertEquals(1, totalRankService.add(totalRank21));
        assertEquals(1, totalRankService.add(totalRank22));

        totalRankService.setCurrentBatch(2);
        Results<RankedUser> results = totalRankService.getUsers(new Page());
        validateResults(results, rankedUser1, rankedUser2);
    }

    @Test
    public void testAddConflict() {
        assertEquals(1, totalRankService.add(totalRank11));
        assertEquals(0, totalRankService.add(totalRank11));
    }

    @Test
    public void testAgeOff() {
        assertEquals(1, totalRankService.add(totalRank11));
        assertEquals(1, totalRankService.add(totalRank12));
        assertEquals(1, totalRankService.add(totalRank21));
        assertEquals(1, totalRankService.add(totalRank22));

        totalRankService.ageOff(now.minusSeconds(5));

        Results<TotalRank> results = totalRankService.getAll(new Page(), emptyList());
        validateResults(results, totalRank11, totalRank21);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, totalRankService.add(totalRank11));
        assertEquals(1, totalRankService.add(totalRank12));
        assertEquals(1, totalRankService.add(totalRank21));
        assertEquals(1, totalRankService.add(totalRank22));

        totalRankService.truncate();

        Results<TotalRank> results = totalRankService.getAll(new Page(), emptyList());
        validateResults(results);
    }
}
