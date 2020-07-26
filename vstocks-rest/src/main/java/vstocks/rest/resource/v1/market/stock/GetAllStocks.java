package vstocks.rest.resource.v1.market.stock;

import vstocks.db.service.StockService;
import vstocks.model.Results;
import vstocks.model.Stock;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/market/{marketId}/stocks")
public class GetAllStocks extends BaseResource {
    private final StockService stockService;

    @Inject
    public GetAllStocks(StockService stockService) {
        this.stockService = stockService;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Results<Stock> getAllStocks(@PathParam("marketId") String marketId,
                                       @QueryParam("pageNum") Integer pageNum,
                                       @QueryParam("pageSize") Integer pageSize) {
        return stockService.getForMarket(marketId, getPage(pageNum, pageSize));
    }
}
