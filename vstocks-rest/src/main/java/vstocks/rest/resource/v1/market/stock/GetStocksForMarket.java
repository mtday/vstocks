package vstocks.rest.resource.v1.market.stock;

import vstocks.model.Market;
import vstocks.model.Results;
import vstocks.model.Stock;
import vstocks.rest.resource.BaseResource;
import vstocks.service.db.DatabaseServiceFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/market/{marketId}/stocks")
@Singleton
public class GetStocksForMarket extends BaseResource {
    private final DatabaseServiceFactory databaseServiceFactory;

    @Inject
    public GetStocksForMarket(DatabaseServiceFactory databaseServiceFactory) {
        this.databaseServiceFactory = databaseServiceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Results<Stock> getAllStocks(@PathParam("marketId") String marketId,
                                       @QueryParam("pageNum") Integer pageNum,
                                       @QueryParam("pageSize") Integer pageSize) {
        Market market = Market.from(marketId)
                .orElseThrow(() -> new NotFoundException("Market " + marketId + " not found"));
        return databaseServiceFactory.getStockService().getForMarket(market, getPage(pageNum, pageSize));
    }
}
