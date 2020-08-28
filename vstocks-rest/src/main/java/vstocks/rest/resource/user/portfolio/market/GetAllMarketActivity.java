package vstocks.rest.resource.user.portfolio.market;

import vstocks.db.ServiceFactory;
import vstocks.model.ActivityLog;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.User;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static vstocks.model.ActivityType.STOCK_BUY;
import static vstocks.model.ActivityType.STOCK_SELL;

@Path("/user/portfolio/market/activity")
@Singleton
public class GetAllMarketActivity extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetAllMarketActivity(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public Results<ActivityLog> getMarketActivity(@Context SecurityContext securityContext,
                                                  @QueryParam("pageNum") Integer pageNum,
                                                  @QueryParam("pageSize") Integer pageSize,
                                                  @QueryParam("sort") String sort) {
        User user = getUser(securityContext);
        Page page = getPage(pageNum, pageSize);
        return serviceFactory.getActivityLogService()
                .getForUser(user.getId(), Set.of(STOCK_BUY, STOCK_SELL), page, getSort(sort));
    }
}
