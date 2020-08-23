package vstocks.db.portfolio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.*;
import vstocks.model.portfolio.TotalRank;
import vstocks.model.portfolio.TotalRankCollection;

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
            .setUserId(user1.getId())
            .setTimestamp(now)
            .setRank(1);
    private final TotalRank totalRank12 = new TotalRank()
            .setUserId(user1.getId())
            .setTimestamp(now.minusSeconds(10))
            .setRank(2);
    private final TotalRank totalRank21 = new TotalRank()
            .setUserId(user2.getId())
            .setTimestamp(now)
            .setRank(2);
    private final TotalRank totalRank22 = new TotalRank()
            .setUserId(user2.getId())
            .setTimestamp(now.minusSeconds(10))
            .setRank(3);

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

        Results<TotalRank> results = totalRankService.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().stream().map(TotalRank::getRank).allMatch(rank -> rank == 1));
    }

    @Test
    public void testGenerateNoTie() {
        userCreditsService.update(user1.getId(), 10);

        assertEquals(2, totalRankService.generate());

        Results<TotalRank> results = totalRankService.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(3, results.getResults().stream().mapToLong(TotalRank::getRank).sum());
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
        validateResults(latest.getRanks(), totalRank11, totalRank12);
        assertEquals(getDeltas(-1, -50f), latest.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<TotalRank> results = totalRankService.getAll(new Page(), emptySet());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, totalRankService.add(totalRank11));
        assertEquals(1, totalRankService.add(totalRank12));

        Results<TotalRank> results = totalRankService.getAll(new Page(), emptySet());
        validateResults(results, totalRank11, totalRank12);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, totalRankService.add(totalRank11));
        assertEquals(1, totalRankService.add(totalRank12));
        assertEquals(1, totalRankService.add(totalRank21));
        assertEquals(1, totalRankService.add(totalRank22));

        Set<Sort> sort = new LinkedHashSet<>(asList(RANK.toSort(DESC), USER_ID.toSort()));
        Results<TotalRank> results = totalRankService.getAll(new Page(), sort);
        validateResults(results, totalRank22, totalRank12, totalRank21, totalRank11);
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

        Results<TotalRank> results = totalRankService.getAll(new Page(), emptySet());
        validateResults(results, totalRank11, totalRank21);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, totalRankService.add(totalRank11));
        assertEquals(1, totalRankService.add(totalRank12));
        assertEquals(1, totalRankService.add(totalRank21));
        assertEquals(1, totalRankService.add(totalRank22));

        totalRankService.truncate();

        Results<TotalRank> results = totalRankService.getAll(new Page(), emptySet());
        validateResults(results);
    }
}
