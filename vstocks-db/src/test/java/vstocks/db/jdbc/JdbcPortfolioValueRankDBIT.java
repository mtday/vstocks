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
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Sort.SortDirection.DESC;

public class JdbcPortfolioValueRankDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private PortfolioValueRankTable portfolioValueRankTable;

    private JdbcPortfolioValueRankDB portfolioValueRankDB;

    private final Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private final User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
    private final User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");

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

    @Test
    public void testGetLatestMissing() {
        assertFalse(portfolioValueRankDB.getLatest("missing-id").isPresent());
    }

    @Test
    public void testGetLatestExists() {
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank));

        Optional<PortfolioValueRank> fetched = portfolioValueRankDB.getLatest(user1.getId());
        assertTrue(fetched.isPresent());
        assertEquals(portfolioValueRank.getUserId(), fetched.get().getUserId());
        assertEquals(portfolioValueRank.getTimestamp(), fetched.get().getTimestamp());
        assertEquals(portfolioValueRank.getRank(), fetched.get().getRank());
    }

    @Test
    public void testGetLatestNone() {
        Results<PortfolioValueRank> results = portfolioValueRankDB.getLatest(singleton(user1.getId()), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetLatestSomeNoSort() {
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now.minusSeconds(20)).setRank(20);
        PortfolioValueRank portfolioValueRank3 = new PortfolioValueRank().setUserId(user2.getId()).setTimestamp(now).setRank(30);
        PortfolioValueRank portfolioValueRank4 = new PortfolioValueRank().setUserId(user2.getId()).setTimestamp(now.minusSeconds(40)).setRank(40);
        assertEquals(4, portfolioValueRankDB.addAll(asList(portfolioValueRank1, portfolioValueRank2, portfolioValueRank3, portfolioValueRank4)));

        Results<PortfolioValueRank> results = portfolioValueRankDB.getLatest(asList(user1.getId(), user2.getId()), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(portfolioValueRank1, results.getResults().get(0));
        assertEquals(portfolioValueRank3, results.getResults().get(1));
    }

    @Test
    public void testGetLatestSomeWithSort() {
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now.minusSeconds(20)).setRank(20);
        PortfolioValueRank portfolioValueRank3 = new PortfolioValueRank().setUserId(user2.getId()).setTimestamp(now).setRank(30);
        PortfolioValueRank portfolioValueRank4 = new PortfolioValueRank().setUserId(user2.getId()).setTimestamp(now.minusSeconds(40)).setRank(40);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank1));
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank2));
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank3));
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank4));

        Set<Sort> sort = new LinkedHashSet<>(asList(RANK.toSort(DESC), USER_ID.toSort(DESC)));
        Results<PortfolioValueRank> results = portfolioValueRankDB.getLatest(asList(user1.getId(), user2.getId()), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(portfolioValueRank3, results.getResults().get(0));
        assertEquals(portfolioValueRank1, results.getResults().get(1));
    }

    @Test
    public void testGetForUserNone() {
        Results<PortfolioValueRank> results = portfolioValueRankDB.getForUser(user1.getId(), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForUserSomeNoSort() {
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now.minusSeconds(10)).setRank(20);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank1));
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank2));

        Results<PortfolioValueRank> results = portfolioValueRankDB.getForUser(user1.getId(), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(portfolioValueRank1, results.getResults().get(0));
        assertEquals(portfolioValueRank2, results.getResults().get(1));
    }

    @Test
    public void testGetForUserSomeWithSort() {
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now.minusSeconds(10)).setRank(20);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank1));
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank2));

        Set<Sort> sort = new LinkedHashSet<>(asList(RANK.toSort(DESC), USER_ID.toSort(DESC)));
        Results<PortfolioValueRank> results = portfolioValueRankDB.getForUser(user1.getId(), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(portfolioValueRank2, results.getResults().get(0));
        assertEquals(portfolioValueRank1, results.getResults().get(1));
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
        List<PortfolioValueRank> list = new ArrayList<>();
        assertEquals(0, portfolioValueRankDB.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(user2.getId()).setTimestamp(now.minusSeconds(10)).setRank(20);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank1));
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank2));

        List<PortfolioValueRank> list = new ArrayList<>();
        assertEquals(2, portfolioValueRankDB.consume(list::add, emptySet()));
        assertEquals(2, list.size());
        assertEquals(portfolioValueRank1, list.get(0));
        assertEquals(portfolioValueRank2, list.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(user2.getId()).setTimestamp(now.minusSeconds(10)).setRank(20);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank1));
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank2));

        List<PortfolioValueRank> list = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(RANK.toSort(DESC), USER_ID.toSort(DESC)));
        assertEquals(2, portfolioValueRankDB.consume(list::add, sort));
        assertEquals(2, list.size());
        assertEquals(portfolioValueRank2, list.get(0));
        assertEquals(portfolioValueRank1, list.get(1));
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

        Optional<PortfolioValueRank> fetched = portfolioValueRankDB.getLatest(user1.getId());
        assertTrue(fetched.isPresent());
        assertEquals(portfolioValueRank.getUserId(), fetched.get().getUserId());
        assertEquals(portfolioValueRank.getTimestamp(), fetched.get().getTimestamp());
        assertEquals(portfolioValueRank.getRank(), fetched.get().getRank());
    }

    @Test
    public void testAddConflictDifferentValues() {
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank));
        portfolioValueRank.setRank(12);
        assertEquals(1, portfolioValueRankDB.add(portfolioValueRank));

        Optional<PortfolioValueRank> fetched = portfolioValueRankDB.getLatest(user1.getId());
        assertTrue(fetched.isPresent());
        assertEquals(portfolioValueRank.getUserId(), fetched.get().getUserId());
        assertEquals(portfolioValueRank.getTimestamp(), fetched.get().getTimestamp());
        assertEquals(portfolioValueRank.getRank(), fetched.get().getRank());
    }

    @Test
    public void testAddAll() {
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(user1.getId()).setTimestamp(now).setRank(10);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(user2.getId()).setTimestamp(now.minusSeconds(10)).setRank(20);
        assertEquals(2, portfolioValueRankDB.addAll(asList(portfolioValueRank1, portfolioValueRank2)));

        Results<PortfolioValueRank> results = portfolioValueRankDB.getLatest(asList(user1.getId(), user2.getId()), new Page(), emptySet());
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
