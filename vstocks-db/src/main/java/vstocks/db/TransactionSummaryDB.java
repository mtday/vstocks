package vstocks.db;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.TransactionSummary;

import java.time.Instant;
import java.util.Set;

public interface TransactionSummaryDB {
    TransactionSummary generate();

    TransactionSummary getLatest();

    Results<TransactionSummary> getAll(Page page, Set<Sort> sort);

    int add(TransactionSummary transactionSummary);

    int ageOff(Instant cutoff);
}
