package vstocks.rest.resource.market;

import vstocks.model.Market;
import vstocks.rest.resource.BaseResource;

import javax.inject.Singleton;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/market/{market}")
@Singleton
public class GetMarket extends BaseResource {
    @GET
    @Produces(APPLICATION_JSON)
    public Market getMarket(@PathParam("market") String market) {
        return Market.from(market).orElseThrow(() -> new NotFoundException("Market " + market + " not found"));
    }
}
