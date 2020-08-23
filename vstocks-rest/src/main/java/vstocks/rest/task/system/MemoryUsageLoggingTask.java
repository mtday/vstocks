package vstocks.rest.task.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.rest.task.TwoMinuteTask;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

public class MemoryUsageLoggingTask extends TwoMinuteTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryUsageLoggingTask.class);

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
