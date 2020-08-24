package vstocks.rest.resource.dashboard;

import vstocks.db.ServiceFactory;
import vstocks.model.Market;
import vstocks.model.system.OverallMarketValueCollection;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/dashboard/overall/market/{market}")
@Singleton
public class GetOverallMarketValue extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetOverallMarketValue(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public OverallMarketValueCollection getOverallMarket(@PathParam("market") String marketStr) {
        Market market = Market.from(marketStr)
                .orElseThrow(() -> new NotFoundException("Market " + marketStr + " not found"));
        return serviceFactory.getOverallMarketValueService().getLatest(market);
    }
}
