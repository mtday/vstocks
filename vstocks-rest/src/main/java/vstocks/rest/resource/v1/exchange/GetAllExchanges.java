package vstocks.rest.resource.v1.exchange;

import vstocks.model.Exchange;
import vstocks.db.ExchangeStore;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/exchange")
public class GetAllExchanges {
    private final ExchangeStore exchangeStore;

    @Inject
    public GetAllExchanges(ExchangeStore exchangeStore) {
        this.exchangeStore = exchangeStore;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public List<Exchange> getAllExchanges() {
        return exchangeStore.getAll();
    }
}
