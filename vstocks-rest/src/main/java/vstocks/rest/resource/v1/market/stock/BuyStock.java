package vstocks.rest.resource.v1.market.stock;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import vstocks.model.Market;
import vstocks.model.PricedUserStock;
import vstocks.rest.resource.BaseResource;
import vstocks.service.db.DatabaseServiceFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.WILDCARD;

@Path("/v1/market/{market}/stock/{symbol}/buy/{shares:[0-9]+}")
@Singleton
public class BuyStock extends BaseResource {
    private final DatabaseServiceFactory databaseServiceFactory;

    @Inject
    public BuyStock(DatabaseServiceFactory databaseServiceFactory) {
        this.databaseServiceFactory = databaseServiceFactory;
    }

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(WILDCARD)
    @Pac4JSecurity(authorizers = "isAuthenticated")
    public PricedUserStock buyStock(@PathParam("market") String marketId,
                                    @PathParam("symbol") String symbol,
                                    @PathParam("shares") int shares,
                                    @Pac4JProfile CommonProfile profile) {
        Market market = Market.from(marketId)
                .orElseThrow(() -> new NotFoundException("Market " + marketId + " not found"));
        String userId = getUser(profile).getId();
        if (databaseServiceFactory.getUserStockService().buyStock(userId, market, symbol, shares) == 0) {
            throw new BadRequestException("Failed to buy " + shares + " shares of " + market + "/" + symbol + " stock");
        }
        return databaseServiceFactory.getPricedUserStockService().get(userId, market, symbol)
                .orElseThrow(() -> new NotFoundException("Stock " + market + "/" + symbol + " not found"));
    }
}
