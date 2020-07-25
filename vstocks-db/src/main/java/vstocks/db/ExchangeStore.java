package vstocks.db;

import vstocks.model.Exchange;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

public interface ExchangeStore {
    Optional<Exchange> get(String id);

    List<Exchange> getAll();

    int add(Collection<Exchange> exchanges);
    default int add(Exchange... exchanges) {
        return add(asList(exchanges));
    }

    int update(Collection<Exchange> exchanges);
    default int update(Exchange... exchanges) {
        return update(asList(exchanges));
    }

    int delete(Collection<String> ids);
    default int delete(String... ids) {
        return delete(asList(ids));
    }

    int truncate();
}
