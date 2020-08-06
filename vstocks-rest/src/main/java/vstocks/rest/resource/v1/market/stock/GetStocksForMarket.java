package vstocks.rest.resource.v1.market.stock;

import vstocks.model.Market;
import vstocks.model.PricedStock;
import vstocks.model.Results;
import vstocks.rest.resource.BaseResource;
import vstocks.db.DBFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/market/{marketId}/stocks")
@Singleton
public class GetStocksForMarket extends BaseResource {
    private final DBFactory dbFactory;

    @Inject
    public GetStocksForMarket(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
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
        return dbFactory.getPricedStockDB().getForMarket(market, active, getPage(pageNum, pageSize), getSort(sort));
    }
}
