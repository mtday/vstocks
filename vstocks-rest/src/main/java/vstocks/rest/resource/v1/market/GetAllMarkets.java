package vstocks.rest.resource.v1.market;

import vstocks.model.Market;
import vstocks.model.Results;
import vstocks.rest.resource.BaseResource;
import vstocks.service.db.DatabaseServiceFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/markets")
@Singleton
public class GetAllMarkets extends BaseResource {
    private final DatabaseServiceFactory databaseServiceFactory;

    @Inject
    public GetAllMarkets(DatabaseServiceFactory databaseServiceFactory) {
        this.databaseServiceFactory = databaseServiceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Results<Market> getAllMarkets(@QueryParam("pageNum") Integer pageNum,
                                         @QueryParam("pageSize") Integer pageSize) {
        return databaseServiceFactory.getMarketService().getAll(getPage(pageNum, pageSize));
    }
}
