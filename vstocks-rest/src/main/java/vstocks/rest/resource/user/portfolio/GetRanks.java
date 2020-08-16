package vstocks.rest.resource.user.portfolio;

import vstocks.db.DBFactory;
import vstocks.model.PortfolioValueRank;
import vstocks.model.User;
import vstocks.model.rest.UserPortfolioRankResponse;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.time.Instant;
import java.util.List;

import static java.util.Collections.emptySet;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/user/portfolio/ranks")
@Singleton
public class GetRanks extends BaseResource {
    private static final int HISTORY_DAYS = 30;

    private final DBFactory dbFactory;

    @Inject
    public GetRanks(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public UserPortfolioRankResponse getPortfolio(@Context SecurityContext securityContext) {
        User user = getUser(securityContext);

        Instant earliest = getEarliest(HISTORY_DAYS);

        List<PortfolioValueRank> historicalRanks =
                dbFactory.getPortfolioValueRankDB().getForUserSince(user.getId(), earliest, emptySet());
        PortfolioValueRank currentRank = historicalRanks.stream().findFirst().orElse(null);

        return new UserPortfolioRankResponse()
                .setCurrentRank(currentRank)
                .setHistoricalRanks(historicalRanks);
    }

}
