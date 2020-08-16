package vstocks.rest.resource.user.portfolio;

import vstocks.db.DBFactory;
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

@Path("/user/stocks")
@Singleton
public class GetStocks extends BaseResource {
    private final DBFactory dbFactory;

    @Inject
    public GetStocks(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public Results<PricedUserStock> getStocks(@Context SecurityContext securityContext,
                                              @QueryParam("pageNum") Integer pageNum,
                                              @QueryParam("sizeSize") Integer pageSize,
                                              @QueryParam("sort") String sort) {
        User user = getUser(securityContext);
        return dbFactory.getPricedUserStockDB().getForUser(user.getId(), getPage(pageNum, pageSize), getSort(sort));
    }
}
