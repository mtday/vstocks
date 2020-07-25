package vstocks.db.impl;

import vstocks.db.ExchangeStore;
import vstocks.model.Exchange;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class JdbcExchangeStore extends BaseStore<Exchange> implements ExchangeStore {
    public JdbcExchangeStore(DataSource dataSource) {
        super(dataSource);
    }

    private static final RowMapper<Exchange> ROW_MAPPER = rs ->
            new Exchange()
                    .setId(rs.getString("id"))
                    .setName(rs.getString("name"));

    private static final RowSetter<Exchange> INSERT_ROW_SETTER = (ps, exchange) -> {
        int index = 0;
        ps.setString(++index, exchange.getId());
        ps.setString(++index, exchange.getName());
    };

    private static final RowSetter<Exchange> UPDATE_ROW_SETTER = (ps, exchange) -> {
        int index = 0;
        ps.setString(++index, exchange.getName());
        ps.setString(++index, exchange.getId());
    };

    @Override
    public Optional<Exchange> get(String id) {
        return getOne(ROW_MAPPER, "SELECT * FROM exchanges WHERE id = ?", id);
    }

    @Override
    public List<Exchange> getAll() {
        return getList(ROW_MAPPER, "SELECT * FROM exchanges");
    }

    @Override
    public int add(Collection<Exchange> exchanges) {
        return update(INSERT_ROW_SETTER, "INSERT INTO exchanges (id, name) VALUES (?, ?)", exchanges);
    }

    @Override
    public int update(Collection<Exchange> exchanges) {
        return update(UPDATE_ROW_SETTER, "UPDATE exchanges SET name = ? WHERE id = ?", exchanges);
    }

    @Override
    public int delete(Collection<String> ids) {
        return update("DELETE FROM exchanges WHERE id = ANY(?)", ids);
    }

    @Override
    public int truncate() {
        return update("DELETE FROM exchanges");
    }
}
