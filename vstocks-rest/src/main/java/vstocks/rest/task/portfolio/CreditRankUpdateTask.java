package vstocks.rest.task.portfolio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.rest.Environment;
import vstocks.rest.task.HourlyTask;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static vstocks.config.Config.DATA_HISTORY_DAYS;

public class CreditRankUpdateTask extends HourlyTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreditRankUpdateTask.class);

    private final Environment environment;

    public CreditRankUpdateTask(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run() {
        environment.getServiceFactory().getCreditRankService().generate();

        int days = DATA_HISTORY_DAYS.getInt();
        Instant cutoff = Instant.now().truncatedTo(DAYS).minus(days, DAYS);
        environment.getServiceFactory().getCreditRankService().ageOff(cutoff);
    }
}
