package vstocks.rest.resource.v1.market.stock;

import vstocks.model.Stock;
import vstocks.rest.resource.BaseResource;
import vstocks.service.db.DatabaseServiceFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/market/{marketId}/stock/{stockId}")
@Singleton
public class GetStock extends BaseResource {
    private final DatabaseServiceFactory databaseServiceFactory;

    @Inject
    public GetStock(DatabaseServiceFactory databaseServiceFactory) {
        this.databaseServiceFactory = databaseServiceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Stock getStock(@PathParam("marketId") String marketId,
                          @PathParam("stockId") String stockId) {
        return databaseServiceFactory.getStockService().get(marketId, stockId)
                .orElseThrow(() -> new NotFoundException("Stock " + marketId + "/" + stockId + " not found"));
    }
}
