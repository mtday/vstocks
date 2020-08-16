package vstocks.db.jdbc;

import vstocks.db.PortfolioValueRankDB;
import vstocks.db.jdbc.table.PortfolioValueRankTable;
import vstocks.model.Page;
import vstocks.model.PortfolioValueRank;
import vstocks.model.Results;
import vstocks.model.Sort;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class JdbcPortfolioValueRankDB extends BaseService implements PortfolioValueRankDB {
    private final PortfolioValueRankTable portfolioValueRankTable = new PortfolioValueRankTable();

    public JdbcPortfolioValueRankDB(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<PortfolioValueRank> getLatest(String userId) {
        return withConnection(conn -> portfolioValueRankTable.getLatest(conn, userId));
    }

    @Override
    public Results<PortfolioValueRank> getLatest(Collection<String> userIds, Page page, Set<Sort> sort) {
        return withConnection(conn -> portfolioValueRankTable.getLatest(conn, userIds, page, sort));
    }

    @Override
    public Results<PortfolioValueRank> getForUser(String userId, Page page, Set<Sort> sort) {
        return withConnection(conn -> portfolioValueRankTable.getForUser(conn, userId, page, sort));
    }

    @Override
    public List<PortfolioValueRank> getForUserSince(String userId, Instant earliest, Set<Sort> sort) {
        return withConnection(conn -> portfolioValueRankTable.getForUserSince(conn, userId, earliest, sort));
    }

    @Override
    public Results<PortfolioValueRank> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> portfolioValueRankTable.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<PortfolioValueRank> consumer, Set<Sort> sort) {
        return withConnection(conn -> portfolioValueRankTable.consume(conn, consumer, sort));
    }

    @Override
    public int add(PortfolioValueRank portfolioValueRank) {
        return withConnection(conn -> portfolioValueRankTable.add(conn, portfolioValueRank));
    }

    @Override
    public int addAll(Collection<PortfolioValueRank> portfolioValueRanks) {
        return withConnection(conn -> portfolioValueRankTable.addAll(conn, portfolioValueRanks));
    }

    @Override
    public int ageOff(Instant cutoff) {
        return withConnection(conn -> portfolioValueRankTable.ageOff(conn, cutoff));
    }
}
