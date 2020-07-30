package vstocks.rest.resource.v1.market;

import vstocks.model.Market;
import vstocks.rest.resource.BaseResource;
import vstocks.service.db.DatabaseServiceFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/market/{id}")
@Singleton
public class GetMarket extends BaseResource {
    private final DatabaseServiceFactory databaseServiceFactory;

    @Inject
    public GetMarket(DatabaseServiceFactory databaseServiceFactory) {
        this.databaseServiceFactory = databaseServiceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Market getMarket(@PathParam("id") String id) {
        return databaseServiceFactory.getMarketService().get(id)
                .orElseThrow(() -> new NotFoundException("Market " + id + " not found"));
    }
}
