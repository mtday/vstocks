package vstocks.rest.task;

import java.util.concurrent.ScheduledExecutorService;

public interface Task extends Runnable {
    void schedule(ScheduledExecutorService scheduledExecutorService);
}
