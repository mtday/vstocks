package vstocks.rest.task;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public abstract class HourlyTask implements Task {
    @Override
    public final void schedule(ScheduledExecutorService scheduledExecutorService) {
        // Determine how long to delay so that our scheduled task runs on the hour approximately.
        Instant now = Instant.now();
        Instant previousHour = Instant.now().truncatedTo(HOURS);

        long millisIntoHour = now.toEpochMilli() - previousHour.toEpochMilli();
        long millisInHour = MINUTES.toMillis(60);
        long delay = millisInHour - millisIntoHour;

        scheduledExecutorService.scheduleAtFixedRate(this, delay, MINUTES.toMillis(60), MILLISECONDS);
    }

    @Override
    public abstract void run();
}
