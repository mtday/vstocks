package vstocks.rest.task.system;

import vstocks.rest.Environment;
import vstocks.rest.task.HourlyTask;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static vstocks.config.Config.DATA_HISTORY_DAYS;

public class ActiveUserCountUpdateTask extends HourlyTask {
    private final Environment environment;

    public ActiveUserCountUpdateTask(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run() {
        environment.getServiceFactory().getActiveUserCountService().generate();

        int days = DATA_HISTORY_DAYS.getInt();
        Instant cutoff = Instant.now().truncatedTo(DAYS).minus(days, DAYS);
        environment.getServiceFactory().getActiveUserCountService().ageOff(cutoff);
    }
}
