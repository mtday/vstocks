package vstocks.rest.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.model.PortfolioValue;
import vstocks.model.PortfolioValueRank;
import vstocks.model.PortfolioValueSummary;
import vstocks.rest.Environment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import static java.time.temporal.ChronoField.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class PortfolioValueGenerateTask implements Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioValueGenerateTask.class);
    private static final int BATCH_SIZE = 100;

    private final Environment environment;
    private final int batchSize;

    public PortfolioValueGenerateTask(Environment environment) {
        this(environment, BATCH_SIZE);
    }

    public PortfolioValueGenerateTask(Environment environment, int batchSize) {
        this.environment = environment;
        this.batchSize = batchSize;
    }

    @Override
    public void schedule(ScheduledExecutorService scheduledExecutorService) {
        // Determine how long to delay so that our scheduled task runs at approximately each 10 minute mark.
        LocalDateTime now = LocalDateTime.now();
        int minute = now.get(MINUTE_OF_HOUR) % 10;
        int second = now.get(SECOND_OF_MINUTE);
        int millis = now.get(MILLI_OF_SECOND);
        long delayMinutes = (9 - minute) * 60000;
        long delaySeconds = (second > 0 ? 59 - second : 59) * 1000;
        long delayMillis  = millis > 0 ? 1000 - millis : 1000;
        long delay = delayMinutes + delaySeconds + delayMillis;

        scheduledExecutorService.scheduleAtFixedRate(this, delay, MINUTES.toMillis(10), MILLISECONDS);
    }

    @Override
    public void run() {
        LOGGER.info("Starting portfolio value generation");

        List<PortfolioValue> values = new ArrayList<>();
        List<PortfolioValueRank> ranks = new ArrayList<>();
        AtomicLong rank = new AtomicLong(0);

        environment.getDBFactory().getPortfolioValueDB().generateAll(portfolioValue -> {
            values.add(portfolioValue);

            PortfolioValueRank portfolioValueRank = new PortfolioValueRank()
                    .setUserId(portfolioValue.getUserId())
                    .setRank(rank.incrementAndGet())
                    .setTimestamp(portfolioValue.getTimestamp());
            ranks.add(portfolioValueRank);

            if (values.size() >= batchSize) {
                environment.getDBFactory().getPortfolioValueDB().addAll(values);
                values.clear();
            }
            if (ranks.size() >= batchSize) {
                environment.getDBFactory().getPortfolioValueRankDB().addAll(ranks);
                ranks.clear();
            }
        });
        if (!values.isEmpty()) {
            environment.getDBFactory().getPortfolioValueDB().addAll(values);
        }
        if (!ranks.isEmpty()) {
            environment.getDBFactory().getPortfolioValueRankDB().addAll(ranks);
        }

        PortfolioValueSummary summary = environment.getDBFactory().getPortfolioValueSummaryDB().generate();
        environment.getDBFactory().getPortfolioValueSummaryDB().add(summary);

        LOGGER.info("Completed portfolio value generation");
    }
}
