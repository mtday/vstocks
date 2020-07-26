package vstocks.rest.resource.v1.market;

import vstocks.db.service.MarketService;
import vstocks.model.Market;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/market/{id}")
public class GetMarket extends BaseResource {
    private final MarketService marketService;

    @Inject
    public GetMarket(MarketService marketService) {
        this.marketService = marketService;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Market getMarket(@PathParam("id") String id) {
        return marketService.get(id)
                .orElseThrow(() -> new NotFoundException("Market with id " + id + " not found"));
    }
}
