package vstocks.rest.resource.standings;

import vstocks.db.ServiceFactory;
import vstocks.model.Market;
import vstocks.model.Results;
import vstocks.model.portfolio.RankedUser;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/standings/market/{market}")
@Singleton
public class GetMarketStandings extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetMarketStandings(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public Results<RankedUser> getMarketStandings(@Context SecurityContext securityContext,
                                                  @PathParam("market") String marketStr,
                                                  @QueryParam("pageNum") Integer pageNum,
                                                  @QueryParam("pageSize") Integer pageSize) {
        Market market = Market.from(marketStr)
                .orElseThrow(() -> new NotFoundException("Market " + marketStr + " not found"));
        return serviceFactory.getMarketRankService().getUsers(market, getPage(pageNum, pageSize));
    }
}
