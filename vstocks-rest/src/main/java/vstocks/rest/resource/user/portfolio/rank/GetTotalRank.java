package vstocks.rest.resource.user.portfolio.rank;

import vstocks.db.ServiceFactory;
import vstocks.model.portfolio.TotalRankCollection;
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

@Path("/user/portfolio/rank/total")
@Singleton
public class GetTotalRank extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetTotalRank(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public TotalRankCollection getTotalRank(@Context SecurityContext securityContext) {
        return serviceFactory.getTotalRankService().getLatest(getUser(securityContext).getId());
    }
}
