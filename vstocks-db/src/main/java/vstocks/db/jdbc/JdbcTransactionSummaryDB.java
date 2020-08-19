package vstocks.db.jdbc;

import vstocks.db.TransactionSummaryDB;
import vstocks.db.jdbc.table.TransactionSummaryTable;
import vstocks.model.Page;
import vstocks.model.TransactionSummary;
import vstocks.model.Results;
import vstocks.model.Sort;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Set;

public class JdbcTransactionSummaryDB extends BaseService implements TransactionSummaryDB {
    private final TransactionSummaryTable transactionSummaryTable = new TransactionSummaryTable();

    public JdbcTransactionSummaryDB(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public TransactionSummary generate() {
        return withConnection(transactionSummaryTable::generate);
    }

    @Override
    public TransactionSummary getLatest() {
        return withConnection(transactionSummaryTable::getLatest);
    }

    @Override
    public Results<TransactionSummary> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> transactionSummaryTable.getAll(conn, page, sort));
    }

    @Override
    public int add(TransactionSummary transactionSummary) {
        return withConnection(conn -> transactionSummaryTable.add(conn, transactionSummary));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> transactionSummaryTable.ageOff(conn, cutoff));
    }
}
