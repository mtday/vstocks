package vstocks.rest.task.portfolio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.rest.Environment;
import vstocks.rest.task.HourlyTask;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static vstocks.config.Config.DATA_HISTORY_DAYS;

public class CreditValueUpdateTask extends HourlyTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreditValueUpdateTask.class);

    private final Environment environment;

    public CreditValueUpdateTask(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run() {
        environment.getServiceFactory().getCreditValueService().generate();

        int days = DATA_HISTORY_DAYS.getInt();
        Instant cutoff = Instant.now().truncatedTo(DAYS).minus(days, DAYS);
        environment.getServiceFactory().getCreditValueService().ageOff(cutoff);
    }
}
