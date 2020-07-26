package vstocks.rest.resource.v1.market;

import vstocks.model.Market;
import vstocks.model.Results;
import vstocks.rest.resource.BaseResource;
import vstocks.service.ServiceFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/markets")
public class GetAllMarkets extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetAllMarkets(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Results<Market> getAllMarkets(@QueryParam("pageNum") Integer pageNum,
                                         @QueryParam("pageSize") Integer pageSize) {
        return serviceFactory.getMarketService().getAll(getPage(pageNum, pageSize));
    }
}
