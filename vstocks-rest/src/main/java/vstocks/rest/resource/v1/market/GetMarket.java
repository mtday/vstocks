package vstocks.rest.resource.v1.market;

import vstocks.model.Market;
import vstocks.rest.resource.BaseResource;

import javax.inject.Singleton;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/market/{id}")
@Singleton
public class GetMarket extends BaseResource {
    @GET
    @Produces(APPLICATION_JSON)
    public Market getMarket(@PathParam("id") String id) {
        try {
            return Market.valueOf(id);
        } catch (IllegalArgumentException bad) {
            throw new NotFoundException("Market " + id + " not found");
        }
    }
}
