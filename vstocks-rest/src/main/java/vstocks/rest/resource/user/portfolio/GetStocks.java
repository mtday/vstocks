package vstocks.rest.resource.user.portfolio;

import vstocks.db.ServiceFactory;
import vstocks.model.Page;
import vstocks.model.PricedUserStock;
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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/user/portfolio/stocks")
@Singleton
public class GetStocks extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetStocks(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public Results<PricedUserStock> getStocks(@Context SecurityContext securityContext,
                                              @QueryParam("pageNum") Integer pageNum,
                                              @QueryParam("pageSize") Integer pageSize,
                                              @QueryParam("sort") String sort) {
        User user = getUser(securityContext);
        Page page = getPage(pageNum, pageSize);
        return serviceFactory.getPricedUserStockService().getForUser(user.getId(), page, getSort(sort));
    }
}
