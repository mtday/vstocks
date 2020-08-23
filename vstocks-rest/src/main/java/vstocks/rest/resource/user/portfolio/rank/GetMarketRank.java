package vstocks.rest.resource.user.portfolio.rank;

import vstocks.db.ServiceFactory;
import vstocks.model.Market;
import vstocks.model.portfolio.MarketRankCollection;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/user/portfolio/rank/market/{market}")
@Singleton
public class GetMarketRank extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetMarketRank(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public MarketRankCollection getMarketRank(@Context SecurityContext securityContext,
                                              @PathParam("market") String marketStr) {
        Market market = Market.from(marketStr)
                .orElseThrow(() -> new NotFoundException("Market " + marketStr + " not found"));
        return serviceFactory.getMarketRankService().getLatest(getUser(securityContext).getId(), market);
    }
}
