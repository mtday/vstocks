package vstocks.db.impl;

import vstocks.db.SymbolStore;
import vstocks.model.Symbol;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class JdbcSymbolStore extends BaseStore<Symbol> implements SymbolStore {
    public JdbcSymbolStore(DataSource dataSource) {
        super(dataSource);
    }

    private static final RowMapper<Symbol> ROW_MAPPER = rs ->
            new Symbol()
                    .setExchangeId(rs.getString("exchange_id"))
                    .setId(rs.getString("id"))
                    .setSymbol(rs.getString("symbol"))
                    .setName(rs.getString("name"));

    private static final RowSetter<Symbol> INSERT_ROW_SETTER = (ps, symbol) -> {
        int index = 0;
        ps.setString(++index, symbol.getExchangeId());
        ps.setString(++index, symbol.getId());
        ps.setString(++index, symbol.getSymbol());
        ps.setString(++index, symbol.getName());
    };

    private static final RowSetter<Symbol> UPDATE_ROW_SETTER = (ps, symbol) -> {
        int index = 0;
        ps.setString(++index, symbol.getSymbol());
        ps.setString(++index, symbol.getName());
        ps.setString(++index, symbol.getExchangeId());
        ps.setString(++index, symbol.getId());
    };

    @Override
    public Optional<Symbol> get(String exchangeId, String id) {
        return getOne(ROW_MAPPER, "SELECT * FROM symbols WHERE exchange_id = ? AND id = ?", exchangeId, id);
    }

    @Override
    public List<Symbol> getAll(String exchangeId) {
        return getList(ROW_MAPPER, "SELECT * FROM symbols WHERE exchange_id = ?", exchangeId);
    }

    @Override
    public List<Symbol> getAll() {
        return getList(ROW_MAPPER, "SELECT * FROM symbols");
    }

    @Override
    public int add(Collection<Symbol> symbols) {
        return update(INSERT_ROW_SETTER, "INSERT INTO symbols (exchange_id, id, symbol, name) VALUES (?, ?, ?, ?)", symbols);
    }

    @Override
    public int update(Collection<Symbol> symbols) {
        return update(UPDATE_ROW_SETTER, "UPDATE symbols SET symbol = ?, name = ? WHERE exchange_id = ? AND id = ?", symbols);
    }

    @Override
    public int delete(String exchangeId, Collection<String> ids) {
        return update("DELETE FROM symbols WHERE exchange_id = ? AND id = ANY(?)", exchangeId, ids);
    }

    @Override
    public int truncate() {
        return update("DELETE FROM symbols");
    }
}
