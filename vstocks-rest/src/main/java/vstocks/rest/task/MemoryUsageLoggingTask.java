package vstocks.rest.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;

import static java.time.temporal.ChronoField.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class MemoryUsageLoggingTask implements Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryUsageLoggingTask.class);

    @Override
    public void schedule(ScheduledExecutorService scheduledExecutorService) {
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
    public void run() {
        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        double usedHeapMegs = heap.getUsed() / 1024d / 1024d;
        double maxHeapMegs = heap.getMax() / 1024d / 1024d;
        double heapPctUsed = usedHeapMegs / maxHeapMegs * 100d;

        MemoryUsage nonHeap = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        if (nonHeap.getMax() != -1) {
            double usedNonHeapMegs = nonHeap.getUsed() / 1024d / 1024d;
            double maxNonHeapMegs = nonHeap.getMax() / 1024d / 1024d;
            double nonHeapPctUsed = usedNonHeapMegs / maxNonHeapMegs * 100d;

            LOGGER.info(String.format("Memory Usage: Heap %.0fM of %.0fM (%.2f%%), Non Heap %.0fM of %.0fM (%.2f%%)",
                    usedHeapMegs, maxHeapMegs, heapPctUsed, usedNonHeapMegs, maxNonHeapMegs, nonHeapPctUsed));
        } else {
            LOGGER.info(String.format("Memory Usage: Heap %.0fM of %.0fM (%.2f%%)", usedHeapMegs, maxHeapMegs, heapPctUsed));
        }
    }
}
