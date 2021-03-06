package vstocks.rest.resource.user.portfolio.market.stock;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import vstocks.model.Market;
import vstocks.model.PricedUserStock;
import vstocks.rest.resource.BaseResource;
import vstocks.db.ServiceFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.WILDCARD;

@Path("/user/portfolio/{market}/stock/{symbol}/buy/{shares:[0-9]+}")
@Singleton
public class BuyStock extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public BuyStock(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(WILDCARD)
    @Pac4JSecurity(authorizers = "isAuthenticated")
    public PricedUserStock buyStock(@PathParam("market") String marketStr,
                                    @PathParam("symbol") String symbol,
                                    @PathParam("shares") long shares,
                                    @Pac4JProfile CommonProfile profile) {
        Market market = Market.from(marketStr)
                .orElseThrow(() -> new NotFoundException("Market " + marketStr + " not found"));
        String userId = getUser(profile).getId();
        if (serviceFactory.getUserStockService().buyStock(userId, market, symbol, shares) == 0) {
            throw new BadRequestException("Failed to buy " + shares + " shares of " + market + "/" + symbol + " stock");
        }
        return serviceFactory.getPricedUserStockService().get(userId, market, symbol)
                .orElseThrow(() -> new NotFoundException("Stock " + market + "/" + symbol + " not found"));
    }
}
