package vstocks.db;

import vstocks.model.Symbol;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

public interface SymbolStore {
    Optional<Symbol> get(String exchangeId, String id);

    List<Symbol> getAll();

    List<Symbol> getAll(String exchangeId);

    int add(Collection<Symbol> symbols);
    default int add(Symbol... symbols) {
        return add(asList(symbols));
    }

    int update(Collection<Symbol> symbols);
    default int update(Symbol... symbols) {
        return update(asList(symbols));
    }

    int delete(String exchangeId, Collection<String> ids);
    default int delete(String exchangeId, String... ids) {
        return delete(exchangeId, asList(ids));
    }

    int truncate();
}
