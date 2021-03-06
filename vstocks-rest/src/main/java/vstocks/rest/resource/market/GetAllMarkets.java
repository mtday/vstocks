package vstocks.rest.resource.market;

import vstocks.model.Market;
import vstocks.rest.resource.BaseResource;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/markets")
@Singleton
public class GetAllMarkets extends BaseResource {
    @GET
    @Produces(APPLICATION_JSON)
    public Market[] getAllMarkets() {
        return Market.values();
    }
}
