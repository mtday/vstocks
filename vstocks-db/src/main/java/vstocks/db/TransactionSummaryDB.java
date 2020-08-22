package vstocks.db;

import vstocks.model.*;
import vstocks.model.system.TransactionSummary;
import vstocks.model.system.TransactionSummaryCollection;

import java.time.Instant;
import java.util.Set;

public interface TransactionSummaryDB {
    TransactionSummary generate();

    TransactionSummaryCollection getLatest();

    Results<TransactionSummary> getAll(Page page, Set<Sort> sort);

    int add(TransactionSummary transactionSummary);

    int ageOff(Instant cutoff);
}
