package vstocks.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.model.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.CREDITS;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class UserCreditsServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private UserCreditsService userCreditsService;

    private final User user1 = new User()
            .setId(generateId("user1@domain.com"))
            .setEmail("user1@domain.com")
            .setUsername("name1")
            .setDisplayName("Name1")
            .setProfileImage("link1");
    private final User user2 = new User()
            .setId(generateId("user2@domain.com"))
            .setEmail("user2@domain.com")
            .setUsername("name2")
            .setDisplayName("Name2")
            .setProfileImage("link2");

    private final UserCredits userCredits1 = new UserCredits().setUserId(user1.getId()).setCredits(10);
    private final UserCredits userCredits2 = new UserCredits().setUserId(user2.getId()).setCredits(12);

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        userCreditsService = new UserCreditsServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));
    }

    @After
    public void cleanup() {
        userCreditsService.truncate();
        userService.truncate();
    }

    @Test
    public void testGetMissing() {
        assertFalse(userCreditsService.get("missing-user").isPresent());
    }

    @Test
    public void testGetExists() {
        assertEquals(2, userCreditsService.truncate());

        assertEquals(1, userCreditsService.add(userCredits1));

        UserCredits fetched = userCreditsService.get(user1.getId()).orElse(null);
        assertEquals(userCredits1, fetched);
    }

    @Test
    public void testGetAllNone() {
        assertEquals(2, userCreditsService.truncate());

        Results<UserCredits> results = userCreditsService.getAll(new Page(), emptySet());
        validateResults(results);
    }

    @Test
    public void testGetAllSomeNoSort() {
        assertEquals(2, userCreditsService.truncate());

        assertEquals(1, userCreditsService.add(userCredits1));
        assertEquals(1, userCreditsService.add(userCredits2));

        Results<UserCredits> results = userCreditsService.getAll(new Page(), emptySet());
        validateResults(results, userCredits2, userCredits1);
    }

    @Test
    public void testGetAllSomeWithSort() {
        assertEquals(2, userCreditsService.truncate());

        assertEquals(1, userCreditsService.add(userCredits1));
        assertEquals(1, userCreditsService.add(userCredits2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), CREDITS.toSort()));
        Results<UserCredits> results = userCreditsService.getAll(new Page(), sort);
        validateResults(results, userCredits2, userCredits1);
    }

    @Test
    public void testConsumeNone() {
        assertEquals(2, userCreditsService.truncate());

        List<UserCredits> results = new ArrayList<>();
        assertEquals(0, userCreditsService.consume(results::add, emptySet()));
        validateResults(results);
    }

    @Test
    public void testConsumeSomeNoSort() {
        assertEquals(2, userCreditsService.truncate());

        assertEquals(1, userCreditsService.add(userCredits1));
        assertEquals(1, userCreditsService.add(userCredits2));

        List<UserCredits> results = new ArrayList<>();
        assertEquals(2, userCreditsService.consume(results::add, emptySet()));
        validateResults(results, userCredits2, userCredits1);
    }

    @Test
    public void testConsumeSomeWithSort() {
        assertEquals(2, userCreditsService.truncate());

        assertEquals(1, userCreditsService.add(userCredits1));
        assertEquals(1, userCreditsService.add(userCredits2));

        List<UserCredits> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), CREDITS.toSort()));
        assertEquals(2, userCreditsService.consume(results::add, sort));
        validateResults(results, userCredits2, userCredits1);
    }

    @Test
    public void testSetInitialCreditsNoneExists() {
        assertEquals(2, userCreditsService.truncate());

        assertEquals(1, userCreditsService.setInitialCredits(userCredits1));

        UserCredits fetched = userCreditsService.get(user1.getId()).orElse(null);
        assertEquals(userCredits1, fetched);
    }

    @Test
    public void testSetInitialCreditsAlreadyExists() {
        assertEquals(2, userCreditsService.truncate());

        assertEquals(1, userCreditsService.setInitialCredits(userCredits1));

        UserCredits updatedCredits = new UserCredits().setUserId(user1.getId()).setCredits(50);
        assertEquals(0, userCreditsService.setInitialCredits(updatedCredits));

        UserCredits fetched = userCreditsService.get(user1.getId()).orElse(null);
        assertEquals(userCredits1, fetched); // not updated
    }

    @Test
    public void testAdd() {
        assertEquals(2, userCreditsService.truncate());

        assertEquals(1, userCreditsService.add(userCredits1));

        UserCredits fetched = userCreditsService.get(user1.getId()).orElse(null);
        assertEquals(userCredits1, fetched);
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        userCreditsService.add(userCredits1);
    }

    @Test
    public void testUpdateIncrementMissing() {
        assertEquals(0, userCreditsService.update("missing-id", 10));
    }

    @Test
    public void testUpdateIncrement() {
        assertEquals(2, userCreditsService.truncate());

        assertEquals(1, userCreditsService.add(userCredits1));
        assertEquals(0, userCreditsService.update(userCredits1.getUserId(), -userCredits1.getCredits() - 2));

        assertEquals(1, userCreditsService.update(userCredits1.getUserId(), 10));

        UserCredits fetched = userCreditsService.get(user1.getId()).orElse(null);
        assertNotNull(fetched);
        assertEquals(userCredits1.getCredits() + 10, fetched.getCredits());
    }

    @Test
    public void testUpdateDecrementMissing() {
        assertEquals(0, userCreditsService.update("missing-id", -10));
    }

    @Test
    public void testUpdateDecrementTooFar() {
        assertEquals(2, userCreditsService.truncate());

        assertEquals(1, userCreditsService.add(userCredits1));
        assertEquals(0, userCreditsService.update(userCredits1.getUserId(), -userCredits1.getCredits() - 2));

        UserCredits fetched = userCreditsService.get(user1.getId()).orElse(null);
        assertEquals(userCredits1, fetched); // not actually updated
    }

    @Test
    public void testUpdateDecrement() {
        assertEquals(2, userCreditsService.truncate());

        assertEquals(1, userCreditsService.add(userCredits1));
        assertEquals(1, userCreditsService.update(userCredits1.getUserId(), -userCredits1.getCredits() + 2));

        UserCredits fetched = userCreditsService.get(user1.getId()).orElse(null);
        assertNotNull(fetched);
        assertEquals(2, fetched.getCredits());
    }

    @Test
    public void testUpdateZero() {
        assertEquals(2, userCreditsService.truncate());

        assertEquals(1, userCreditsService.add(userCredits1));
        assertEquals(0, userCreditsService.update(userCredits1.getUserId(), 0));

        UserCredits fetched = userCreditsService.get(user1.getId()).orElse(null);
        assertEquals(userCredits1, fetched);
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, userCreditsService.delete("missing"));
    }

    @Test
    public void testDelete() {
        assertEquals(2, userCreditsService.truncate());

        assertEquals(1, userCreditsService.add(userCredits1));
        assertEquals(1, userCreditsService.delete(userCredits1.getUserId()));
        assertFalse(userCreditsService.get(userCredits1.getUserId()).isPresent());
    }

    @Test
    public void testTruncate() {
        assertTrue(userCreditsService.get(userCredits1.getUserId()).isPresent());
        assertTrue(userCreditsService.get(userCredits2.getUserId()).isPresent());

        assertEquals(2, userCreditsService.truncate());

        assertFalse(userCreditsService.get(userCredits1.getUserId()).isPresent());
        assertFalse(userCreditsService.get(userCredits2.getUserId()).isPresent());
    }
}
