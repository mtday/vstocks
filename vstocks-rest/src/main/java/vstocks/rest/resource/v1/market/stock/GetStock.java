package vstocks.rest.resource.v1.market.stock;

import vstocks.model.Stock;
import vstocks.rest.resource.BaseResource;
import vstocks.service.ServiceFactory;

import javax.inject.Inject;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/market/{marketId}/stock/{stockId}")
public class GetStock extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetStock(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Stock getStock(@PathParam("marketId") String marketId,
                          @PathParam("stockId") String stockId) {
        return serviceFactory.getStockService().get(marketId, stockId)
                .orElseThrow(() -> new NotFoundException("Stock " + marketId + "/" + stockId + " not found"));
    }
}
