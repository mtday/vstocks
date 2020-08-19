package vstocks.rest.resource.user.portfolio;

import vstocks.db.DBFactory;
import vstocks.model.PortfolioValueRankCollection;
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

@Path("/user/portfolio/rank")
@Singleton
public class GetRank extends BaseResource {
    private final DBFactory dbFactory;

    @Inject
    public GetRank(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public PortfolioValueRankCollection getPortfolio(@Context SecurityContext securityContext) {
        return dbFactory.getPortfolioValueRankDB().getLatest(getUser(securityContext).getId());
    }
}
