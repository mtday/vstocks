package vstocks.rest.resource.user.portfolio;

import vstocks.db.DBFactory;
import vstocks.model.PortfolioValue;
import vstocks.model.User;
import vstocks.model.rest.UserPortfolioValueResponse;
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

@Path("/user/portfolio/values")
@Singleton
public class GetValues extends BaseResource {
    private static final int HISTORY_DAYS = 30;

    private final DBFactory dbFactory;

    @Inject
    public GetValues(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public UserPortfolioValueResponse getPortfolio(@Context SecurityContext securityContext) {
        User user = getUser(securityContext);

        Instant earliest = getEarliest(HISTORY_DAYS);

        List<PortfolioValue> historicalValues =
                dbFactory.getPortfolioValueDB().getForUserSince(user.getId(), earliest, emptySet());
        PortfolioValue currentValue = historicalValues.stream().findFirst().orElse(null);

        return new UserPortfolioValueResponse()
                .setCurrentValue(currentValue)
                .setHistoricalValues(historicalValues);
    }

}
