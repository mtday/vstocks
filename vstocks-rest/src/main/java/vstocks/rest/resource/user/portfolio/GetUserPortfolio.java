package vstocks.rest.resource.user.portfolio;

import vstocks.db.DBFactory;
import vstocks.model.PortfolioValue;
import vstocks.model.PortfolioValueRank;
import vstocks.model.User;
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
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.Collections.emptySet;
import static java.util.concurrent.TimeUnit.DAYS;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/user/portfolio")
@Singleton
public class GetUserPortfolio extends BaseResource {
    private static final int PORTFOLIO_VALUE_HISTORY_DAYS = 30;

    private final DBFactory dbFactory;

    @Inject
    public GetUserPortfolio(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    static Instant getEarliest(int days) {
        return Instant.now().minusSeconds(DAYS.toSeconds(days)).truncatedTo(ChronoUnit.DAYS);
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public UserPortfolioResponse getPortfolio(@Context SecurityContext securityContext) {
        User user = getUser(securityContext);

        Instant earliest = getEarliest(PORTFOLIO_VALUE_HISTORY_DAYS);

        List<PortfolioValue> historicalPortfolioValues =
                dbFactory.getPortfolioValueDB().getForUserSince(user.getId(), earliest, emptySet());
        PortfolioValue currentPortfolioValue = historicalPortfolioValues.stream().findFirst().orElse(null);

        List<PortfolioValueRank> historicalPortfolioValueRanks =
                dbFactory.getPortfolioValueRankDB().getForUserSince(user.getId(), earliest, emptySet());
        PortfolioValueRank currentPortfolioValueRank = historicalPortfolioValueRanks.stream().findFirst().orElse(null);

        return new UserPortfolioResponse()
                .setCurrentPortfolioValue(currentPortfolioValue)
                .setHistoricalPortfolioValues(historicalPortfolioValues)
                .setCurrentPortfolioValueRank(currentPortfolioValueRank)
                .setHistoricalPortfolioValueRanks(historicalPortfolioValueRanks);
    }

    public static class UserPortfolioResponse {
        private PortfolioValue currentPortfolioValue;
        private List<PortfolioValue> historicalPortfolioValues;

        private PortfolioValueRank currentPortfolioValueRank;
        private List<PortfolioValueRank> historicalPortfolioValueRanks;

        public UserPortfolioResponse() {
        }

        public PortfolioValue getCurrentPortfolioValue() {
            return currentPortfolioValue;
        }

        public UserPortfolioResponse setCurrentPortfolioValue(PortfolioValue currentPortfolioValue) {
            this.currentPortfolioValue = currentPortfolioValue;
            return this;
        }

        public List<PortfolioValue> getHistoricalPortfolioValues() {
            return historicalPortfolioValues;
        }

        public UserPortfolioResponse setHistoricalPortfolioValues(List<PortfolioValue> historicalPortfolioValues) {
            this.historicalPortfolioValues = historicalPortfolioValues;
            return this;
        }

        public PortfolioValueRank getCurrentPortfolioValueRank() {
            return currentPortfolioValueRank;
        }

        public UserPortfolioResponse setCurrentPortfolioValueRank(PortfolioValueRank currentPortfolioValueRank) {
            this.currentPortfolioValueRank = currentPortfolioValueRank;
            return this;
        }

        public List<PortfolioValueRank> getHistoricalPortfolioValueRanks() {
            return historicalPortfolioValueRanks;
        }

        public UserPortfolioResponse setHistoricalPortfolioValueRanks(List<PortfolioValueRank> historicalPortfolioValueRanks) {
            this.historicalPortfolioValueRanks = historicalPortfolioValueRanks;
            return this;
        }
    }
}
