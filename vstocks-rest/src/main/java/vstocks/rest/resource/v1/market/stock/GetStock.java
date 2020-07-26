package vstocks.rest.resource.v1.market.stock;

import vstocks.db.service.StockService;
import vstocks.model.Stock;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/market/{marketId}/stock/{stockId}")
public class GetStock extends BaseResource {
    private final StockService stockService;

    @Inject
    public GetStock(StockService stockService) {
        this.stockService = stockService;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Stock getStock(@PathParam("marketId") String marketId,
                          @PathParam("stockId") String stockId) {
        return stockService.get(stockId)
                .orElseThrow(() -> new NotFoundException("Stock with id " + stockId + " not found"));
    }
}
