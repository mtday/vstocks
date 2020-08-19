package vstocks.db.jdbc.table;

import vstocks.model.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singleton;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.stream.Collectors.joining;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.SortDirection.DESC;

public class TransactionSummaryTable extends BaseTable {
    private static final RowMapper<TransactionSummary> ROW_MAPPER = rs -> {
        Map<Market, Long> transactions = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> transactions.put(market, 0L));

        Stream.of(rs.getString("transactions"))
                .filter(Objects::nonNull)
                .map(values -> values.split(";"))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .filter(transaction -> !transaction.isEmpty())
                .map(transaction -> transaction.split(":", 2))
                .map(arr -> new SimpleEntry<>(Market.valueOf(arr[0]), Long.parseLong(arr[1])))
                .forEach(entry -> transactions.put(entry.getKey(), entry.getValue()));

        return new TransactionSummary()
                .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                .setTransactions(transactions)
                .setTotal(rs.getLong("total"));
    };

    private static final RowSetter<TransactionSummary> INSERT_ROW_SETTER = (ps, transactionSummary) -> {
        String transactions = Stream.of(transactionSummary.getTransactions())
                .filter(Objects::nonNull)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .map(entry -> entry.getKey().name() + ":" + entry.getValue())
                .collect(joining(";"));

        int index = 0;
        ps.setTimestamp(++index, Timestamp.from(transactionSummary.getTimestamp()));
        ps.setString(++index, transactions);
        ps.setLong(++index, transactionSummary.getTotal());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return singleton(TIMESTAMP.toSort(DESC));
    }

    public TransactionSummary generate(Connection connection) {
        Instant oneDayAgo = Instant.now().truncatedTo(SECONDS).minusSeconds(DAYS.toSeconds(1));
        String sql = "SELECT NOW() AS timestamp,"
                + "       STRING_AGG(market || ':' || transactions, ';') AS transactions,"
                + "       SUM(transactions) AS total FROM ("
                + "  SELECT market, count(*) AS transactions"
                + "  FROM activity_logs"
                + "  WHERE market IS NOT NULL AND timestamp >= ?"
                + "  GROUP BY market"
                + ") AS data";
        return getOne(connection, ROW_MAPPER, sql, oneDayAgo).orElse(null); // there will always be a result
    }

    public TransactionSummary getLatest(Connection connection) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        List<TransactionSummary> values = new ArrayList<>();
        String sql = "SELECT * FROM transaction_summaries WHERE timestamp >= ? ORDER BY timestamp DESC";
        consume(connection, ROW_MAPPER, values::add, sql, earliest);

        TransactionSummary transactionSummary = values.stream().findFirst().orElseGet(() -> {
            Map<Market, Long> transactions = new TreeMap<>();
            Arrays.stream(Market.values()).forEach(market -> transactions.put(market, 0L));
            return new TransactionSummary()
                    .setTimestamp(Instant.now().truncatedTo(SECONDS))
                    .setTransactions(transactions)
                    .setTotal(0);
        });
        transactionSummary.setDeltas(Delta.getDeltas(values, TransactionSummary::getTimestamp, TransactionSummary::getTotal));
        return transactionSummary;
    }

    public Results<TransactionSummary> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM transaction_summaries %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM transaction_summaries";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, TransactionSummary transactionSummary) {
        String sql = "INSERT INTO transaction_summaries (timestamp, transactions, total) VALUES (?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT transaction_summaries_pk DO UPDATE "
                + "SET transactions = EXCLUDED.transactions, total = EXCLUDED.total "
                + "WHERE transaction_summaries.transactions != EXCLUDED.transactions OR "
                + "transaction_summaries.total != EXCLUDED.total";
        return update(connection, INSERT_ROW_SETTER, sql, transactionSummary);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM transaction_summaries WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM transaction_summaries");
    }
}
