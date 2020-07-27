package vstocks.service.jdbc.table;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;

import java.sql.Connection;
import java.util.Optional;

public class MarketTable extends BaseTable<Market> {
    private static final RowMapper<Market> ROW_MAPPER = rs ->
            new Market()
                    .setId(rs.getString("id"))
                    .setName(rs.getString("name"));

    private static final RowSetter<Market> INSERT_ROW_SETTER = (ps, market) -> {
        int index = 0;
        ps.setString(++index, market.getId());
        ps.setString(++index, market.getName());
    };

    private static final RowSetter<Market> UPDATE_ROW_SETTER = (ps, market) -> {
        int index = 0;
        ps.setString(++index, market.getName());
        ps.setString(++index, market.getId());
        ps.setString(++index, market.getName());
    };

    public Optional<Market> get(Connection connection, String id) {
        return getOne(connection, ROW_MAPPER, "SELECT * FROM markets WHERE id = ?", id);
    }

    public Results<Market> getAll(Connection connection, Page page) {
        String query = "SELECT * FROM markets ORDER BY name LIMIT ? OFFSET ?";
        String countQuery = "SELECT COUNT(*) FROM markets";
        return results(connection, ROW_MAPPER, page, query, countQuery);
    }

    public int add(Connection connection, Market market) {
        return update(connection, INSERT_ROW_SETTER, "INSERT INTO markets (id, name) VALUES (?, ?)", market);
    }

    public int update(Connection connection, Market market) {
        return update(connection, UPDATE_ROW_SETTER, "UPDATE markets SET name = ? WHERE id = ? AND name != ?", market);
    }

    public int delete(Connection connection, String id) {
        return update(connection, "DELETE FROM markets WHERE id = ?", id);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM markets");
    }
}
