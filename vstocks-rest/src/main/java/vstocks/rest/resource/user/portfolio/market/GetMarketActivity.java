package vstocks.rest.resource.user.portfolio.market;

import vstocks.db.ServiceFactory;
import vstocks.model.*;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static vstocks.model.ActivityType.STOCK_BUY;
import static vstocks.model.ActivityType.STOCK_SELL;

@Path("/user/portfolio/market/{market}/activity")
@Singleton
public class GetMarketActivity extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetMarketActivity(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public Results<StockActivityLog> getMarketActivity(@Context SecurityContext securityContext,
                                                  @PathParam("market") String marketStr,
                                                  @QueryParam("pageNum") Integer pageNum,
                                                  @QueryParam("pageSize") Integer pageSize,
                                                  @QueryParam("sort") String sort) {
        User user = getUser(securityContext);
        Page page = getPage(pageNum, pageSize);
        Market market = Market.from(marketStr)
                .orElseThrow(() -> new NotFoundException("Market " + marketStr + " not found"));
        return serviceFactory.getStockActivityLogService()
                .getForUser(user.getId(), market, Set.of(STOCK_BUY, STOCK_SELL), page, getSort(sort));
    }
}
