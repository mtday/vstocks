package vstocks.rest.resource.v1.exchange;

import vstocks.model.Exchange;
import vstocks.db.ExchangeStore;

import javax.inject.Inject;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/exchange/{id}")
public class GetExchange {
    private final ExchangeStore exchangeStore;

    @Inject
    public GetExchange(ExchangeStore exchangeStore) {
        this.exchangeStore = exchangeStore;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Exchange getGame(@PathParam("id") String id) {
        return exchangeStore.get(id)
                .orElseThrow(() -> new NotFoundException("Exchange with id " + id + " not found"));
    }
}
