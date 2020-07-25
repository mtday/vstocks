package vstocks.rest.resource.v1.exchange.symbol;

import vstocks.db.SymbolStore;
import vstocks.model.Symbol;

import javax.inject.Inject;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/exchange/{exchangeId}/symbol/{id}")
public class GetSymbol {
    private final SymbolStore symbolStore;

    @Inject
    public GetSymbol(SymbolStore symbolStore) {
        this.symbolStore = symbolStore;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Symbol getSymbol(@PathParam("exchangeId") String exchangeId,
                            @PathParam("id") String id) {
        return symbolStore.get(exchangeId, id)
                .orElseThrow(() -> new NotFoundException("Symbol with id " + id + " not found in exchange " + exchangeId));
    }
}
