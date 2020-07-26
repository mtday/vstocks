package vstocks.rest.resource.v1.market;

import vstocks.db.service.MarketService;
import vstocks.model.Market;
import vstocks.model.Results;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/markets")
public class GetAllMarkets extends BaseResource {
    private final MarketService marketService;

    @Inject
    public GetAllMarkets(MarketService marketService) {
        this.marketService = marketService;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Results<Market> getAllMarkets(@QueryParam("pageNum") Integer pageNum,
                                         @QueryParam("pageSize") Integer pageSize) {
        return marketService.getAll(getPage(pageNum, pageSize));
    }
}
