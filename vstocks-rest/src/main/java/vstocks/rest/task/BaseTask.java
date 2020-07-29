package vstocks.rest.task;

import java.util.concurrent.ScheduledExecutorService;

public interface BaseTask extends Runnable {
    void schedule(ScheduledExecutorService scheduledExecutorService);
}
