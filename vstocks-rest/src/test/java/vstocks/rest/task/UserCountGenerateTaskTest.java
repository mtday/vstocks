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
        UserCount userCount = new UserCount().setTimestamp(Instant.now().truncatedTo(SECONDS)).setUsers(0);
        UserCountDB userCountDB = mock(UserCountDB.class);
        when(userCountDB.generate()).thenReturn(userCount);
        List<UserCount> userCountsAdded = new ArrayList<>();
        when(userCountDB.add(any())).then((Answer<Integer>) invocation -> {
            userCountsAdded.add(invocation.getArgument(0));
            return 1;
        });

        DBFactory dbFactory = mock(DBFactory.class);
        when(dbFactory.getUserCountDB()).thenReturn(userCountDB);

        Environment environment = mock(Environment.class);
        when(environment.getDBFactory()).thenReturn(dbFactory);

        new UserCountGenerateTask(environment).run();

        assertEquals(1, userCountsAdded.size());
        assertEquals(userCount, userCountsAdded.iterator().next());
    }
}
