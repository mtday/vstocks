package vstocks.rest.resource.v1.market.stock;

import vstocks.model.Market;
import vstocks.model.PricedStock;
import vstocks.model.Results;
import vstocks.rest.resource.BaseResource;
import vstocks.service.db.DatabaseServiceFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static java.util.Optional.ofNullable;
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
    public Results<PricedStock> getAllStocks(@PathParam("marketId") String marketId,
                                             @QueryParam("pageNum") Integer pageNum,
                                             @QueryParam("pageSize") Integer pageSize,
                                             @QueryParam("sort") String sort,
                                             @QueryParam("active") String activeParam) {
        Market market = Market.from(marketId)
                .orElseThrow(() -> new NotFoundException("Market " + marketId + " not found"));
        Boolean active = ofNullable(activeParam).map(Boolean::valueOf).orElse(null);
        return databaseServiceFactory.getPricedStockService().getForMarket(market, active, getPage(pageNum, pageSize), getSort(sort));
    }
}
