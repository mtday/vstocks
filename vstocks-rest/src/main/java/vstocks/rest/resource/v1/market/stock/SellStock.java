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
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.WILDCARD;

@Path("/v1/market/{market}/stock/{symbol}/sell/{shares:[0-9]+}")
@Singleton
public class SellStock extends BaseResource {
    private final DatabaseServiceFactory databaseServiceFactory;

    @Inject
    public SellStock(DatabaseServiceFactory databaseServiceFactory) {
        this.databaseServiceFactory = databaseServiceFactory;
    }

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(WILDCARD)
    @Pac4JSecurity(authorizers = "isAuthenticated")
    public PricedUserStock sellStock(@PathParam("market") String marketId,
                                     @PathParam("symbol") String symbol,
                                     @PathParam("shares") int shares,
                                     @Pac4JProfile CommonProfile profile) {
        Market market = Market.from(marketId)
                .orElseThrow(() -> new NotFoundException("Market " + marketId + " not found"));
        String userId = getUser(profile).getId();
        if (databaseServiceFactory.getUserStockService().sellStock(userId, market, symbol, shares) == 0) {
            throw new BadRequestException("Failed to sell " + shares + " shares of " + market + "/" + symbol + " stock");
        }
        return databaseServiceFactory.getPricedUserStockService().get(userId, market, symbol).orElseGet(() -> {
            // Not found likely means the user sold all their shares of stock.
            Instant instant = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            return new PricedUserStock().setUserId(userId).setMarket(market).setSymbol(symbol).setShares(0).setTimestamp(instant).setPrice(1);
        });
    }
}
