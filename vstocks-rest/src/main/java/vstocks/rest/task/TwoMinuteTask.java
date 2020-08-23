package vstocks.rest.task;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;

import static java.time.temporal.ChronoField.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public abstract class TwoMinuteTask implements Task {
    @Override
    public final void schedule(ScheduledExecutorService scheduledExecutorService) {
        // Determine how long to delay so that our scheduled task runs at approximately each even 2 minute mark.
        LocalDateTime now = LocalDateTime.now();
        int minute = now.get(MINUTE_OF_HOUR) % 10;
        int second = now.get(SECOND_OF_MINUTE);
        int millis = now.get(MILLI_OF_SECOND);
        long delayMinutes = (9 - minute) * 60000;
        long delaySeconds = (second > 0 ? 59 - second : 59) * 1000;
        long delayMillis  = millis > 0 ? 1000 - millis : 1000;
        long delay = (delayMinutes + delaySeconds + delayMillis) % MINUTES.toMillis(2);

        scheduledExecutorService.scheduleAtFixedRate(this, delay, MINUTES.toMillis(2), MILLISECONDS);
    }

    @Override
    public abstract void run();
}
