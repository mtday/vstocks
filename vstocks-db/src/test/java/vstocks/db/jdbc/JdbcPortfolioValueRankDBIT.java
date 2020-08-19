package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.jdbc.table.PortfolioValueRankTable;
import vstocks.db.jdbc.table.UserTable;
import vstocks.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.DatabaseField.RANK;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class JdbcPortfolioValueRankDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private PortfolioValueRankTable portfolioValueRankTable;

    private JdbcPortfolioValueRankDB portfolioValueRankDB;

    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final User user1 = new User().setId(generateId("user1@domain.com")).setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
    private final User user2 = new User().setId(generateId("user2@domain.com")).setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");

    @Before
    public void setup() throws SQLException {
        userTable = new UserTable();
        portfolioValueRankTable = new PortfolioValueRankTable();
        portfolioValueRankDB = new JdbcPortfolioValueRankDB(dataSourceExternalResource.get());

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            connection.commit();
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            portfolioValueRankTable.truncate(connection);
            userTable.truncate(connection);
            connection.commit();
        }
    }

    private Map<DeltaInterval, Delta> getDeltas(int change, float percent) {
        return stream(DeltaInterval.values())
                .map(interval -> new Delta().setInterval(interval).setChange(change).setPercent(percent))
                .collect(toMap(Delta::getInterval, identity()));
    }

    @Test
    public void testGetLatestMissing() {
        PortfolioValueRankCollection fetched = portfolioValueRankDB.getLatest("missing-id");
        assertTrue(fetched.getRanks().isEmpty());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }

    @Test
    public void testGetLatestExistsSingle() {
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank));

        PortfolioValueRankCollection fetched = portfolioValueRankDB.getLatest(user1.getId());
        assertEquals(1, fetched.getRanks().size());
        assertEquals(portfolioValueRank, fetched.getRanks().iterator().next());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }

    @Test
    public void testGetLatestExistsMultiple() {
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank));

        PortfolioValueRankCollection fetched = portfolioValueRankDB.getLatest(user1.getId());
        assertEquals(1, fetched.getRanks().size());
        assertEquals(portfolioValueRank, fetched.getRanks().iterator().next());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<PortfolioValueRank> results = portfolioValueRankDB.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(user2.getId()).setTimestamp(now.minusSeconds(10)).setRank(20);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank1));
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank2));

        Results<PortfolioValueRank> results = portfolioValueRankDB.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(portfolioValueRank1, results.getResults().get(0));
        assertEquals(portfolioValueRank2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(user2.getId()).setTimestamp(now.minusSeconds(10)).setRank(20);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank1));
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank2));

        Set<Sort> sort = new LinkedHashSet<>(asList(RANK.toSort(DESC), USER_ID.toSort(DESC)));
        Results<PortfolioValueRank> results = portfolioValueRankDB.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(portfolioValueRank2, results.getResults().get(0));
        assertEquals(portfolioValueRank1, results.getResults().get(1));
    }

    @Test
    public void testConsumeNone() {
        List<PortfolioValueRank> results = new ArrayList<>();
        assertEquals(0, portfolioValueRankDB.consume(results::add, emptySet()));
        assertTrue(results.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(user2.getId()).setTimestamp(now.minusSeconds(10)).setRank(20);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank1));
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank2));

        List<PortfolioValueRank> results = new ArrayList<>();
        assertEquals(2, portfolioValueRankDB.consume(results::add, emptySet()));
        assertEquals(2, results.size());
        assertEquals(portfolioValueRank1, results.get(0));
        assertEquals(portfolioValueRank2, results.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(user2.getId()).setTimestamp(now.minusSeconds(10)).setRank(20);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank1));
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank2));

        List<PortfolioValueRank> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(RANK.toSort(DESC), USER_ID.toSort(DESC)));
        assertEquals(2, portfolioValueRankDB.consume(results::add, sort));
        assertEquals(2, results.size());
        assertEquals(portfolioValueRank2, results.get(0));
        assertEquals(portfolioValueRank1, results.get(1));
    }

    @Test
    public void testAdd() {
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank));
    }

    @Test
    public void testAddConflictSameValues() {
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank));
        assertEquals(0, portfolioValueRankDB.add(portfolioValueRank));

        PortfolioValueRankCollection fetched = portfolioValueRankDB.getLatest(user1.getId());
        assertEquals(1, fetched.getRanks().size());
        assertEquals(portfolioValueRank, fetched.getRanks().iterator().next());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }

    @Test
    public void testAddConflictDifferentValues() {
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank));
        portfolioValueRank.setRank(12);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank));

        PortfolioValueRankCollection fetched = portfolioValueRankDB.getLatest(user1.getId());
        assertEquals(1, fetched.getRanks().size());
        assertEquals(portfolioValueRank, fetched.getRanks().iterator().next());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }

    @Test
    public void testAddAll() {
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(user2.getId()).setTimestamp(now.minusSeconds(10)).setRank(20);
        assertEquals(2, portfolioValueRankDB.addAll(asList(portfolioValueRank1, portfolioValueRank2)));

        Results<PortfolioValueRank> results = portfolioValueRankDB.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(portfolioValueRank1, results.getResults().get(0));
        assertEquals(portfolioValueRank2, results.getResults().get(1));
    }

    @Test
    public void testAgeOff() {
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now.minusSeconds(10)).setRank(10);
        PortfolioValueRank portfolioValueRank3 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now.minusSeconds(20)).setRank(10);

        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank1));
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank2));
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank3));
        assertEquals(2, portfolioValueRankDB.ageOff(now.minusSeconds(5)));

        Results<PortfolioValueRank> results = portfolioValueRankDB.getAll(new Page(), emptySet());
        assertEquals(1, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(portfolioValueRank1, results.getResults().iterator().next());
    }
}
