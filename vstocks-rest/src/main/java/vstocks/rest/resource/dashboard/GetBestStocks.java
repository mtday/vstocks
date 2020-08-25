package vstocks.rest.resource.dashboard;

import vstocks.db.ServiceFactory;
import vstocks.model.Results;
import vstocks.model.StockPriceChange;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/dashboard/stocks/best")
@Singleton
public class GetBestStocks extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetBestStocks(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Results<StockPriceChange> getBestStocks(@QueryParam("pageNum") Integer pageNum,
                                                   @QueryParam("pageSize") Integer pageSize,
                                                   @QueryParam("sort") String sort) {
        return serviceFactory.getStockPriceChangeService().getAll(getPage(pageNum, pageSize), getSort(sort));
    }
}
