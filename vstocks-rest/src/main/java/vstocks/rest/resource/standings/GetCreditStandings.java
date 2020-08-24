package vstocks.rest.resource.standings;

import vstocks.db.ServiceFactory;
import vstocks.model.Results;
import vstocks.model.portfolio.RankedUser;
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

@Path("/standings/credits")
@Singleton
public class GetCreditStandings extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetCreditStandings(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public Results<RankedUser> getCreditsStandings(@Context SecurityContext securityContext,
                                                   @QueryParam("pageNum") Integer pageNum,
                                                   @QueryParam("pageSize") Integer pageSize) {
        return serviceFactory.getCreditRankService().getUsers(getPage(pageNum, pageSize));
    }
}
