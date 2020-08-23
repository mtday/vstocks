package vstocks.rest.resource.user.portfolio.rank;

import vstocks.db.ServiceFactory;
import vstocks.model.portfolio.MarketTotalRankCollection;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/user/portfolio/rank/market-total")
@Singleton
public class GetMarketTotalRank extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetMarketTotalRank(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public MarketTotalRankCollection getMarketTotalRank(@Context SecurityContext securityContext) {
        return serviceFactory.getMarketTotalRankService().getLatest(getUser(securityContext).getId());
    }
}
