package vstocks.rest.resource.v1.exchange.symbol;

import vstocks.db.SymbolStore;
import vstocks.model.Symbol;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/exchange/{exchangeId}/symbol")
public class GetAllSymbols {
    private final SymbolStore symbolStore;

    @Inject
    public GetAllSymbols(SymbolStore symbolStore) {
        this.symbolStore = symbolStore;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public List<Symbol> getAllSymbols(@PathParam("exchangeId") String exchangeId) {
        return symbolStore.getAll(exchangeId);
    }
}
