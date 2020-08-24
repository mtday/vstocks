package vstocks.rest.resource.dashboard;

import vstocks.db.ServiceFactory;
import vstocks.model.system.OverallMarketTotalValueCollection;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/dashboard/overall/market-total")
@Singleton
public class GetOverallMarketTotalValue extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetOverallMarketTotalValue(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public OverallMarketTotalValueCollection getOverallMarketTotals() {
        return serviceFactory.getOverallMarketTotalValueService().getLatest();
    }
}
