package vstocks.rest.task;

import org.junit.Test;
import org.mockito.stubbing.Answer;
import vstocks.db.DBFactory;
import vstocks.db.UserCountDB;
import vstocks.model.UserCount;
import vstocks.rest.Environment;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserCountGenerateTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);
        new UserCountGenerateTask(environment).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(UserCountGenerateTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() {
        UserCount userCountTotal = new UserCount().setTimestamp(Instant.now().truncatedTo(SECONDS)).setUsers(10);
        UserCount userCountActive = new UserCount().setTimestamp(Instant.now().truncatedTo(SECONDS)).setUsers(5);

        UserCountDB userCountDB = mock(UserCountDB.class);
        when(userCountDB.generateTotal()).thenReturn(userCountTotal);
        when(userCountDB.generateActive()).thenReturn(userCountActive);

        List<UserCount> totalUserCountsAdded = new ArrayList<>();
        when(userCountDB.addTotal(any())).then((Answer<Integer>) invocation -> {
            totalUserCountsAdded.add(invocation.getArgument(0));
            return 1;
        });

        List<UserCount> activeUserCountsAdded = new ArrayList<>();
        when(userCountDB.addActive(any())).then((Answer<Integer>) invocation -> {
            activeUserCountsAdded.add(invocation.getArgument(0));
            return 1;
        });

        DBFactory dbFactory = mock(DBFactory.class);
        when(dbFactory.getUserCountDB()).thenReturn(userCountDB);

        Environment environment = mock(Environment.class);
        when(environment.getDBFactory()).thenReturn(dbFactory);

        new UserCountGenerateTask(environment).run();

        assertEquals(1, totalUserCountsAdded.size());
        assertEquals(userCountTotal, totalUserCountsAdded.iterator().next());

        assertEquals(1, activeUserCountsAdded.size());
        assertEquals(userCountActive, activeUserCountsAdded.iterator().next());
    }
}
