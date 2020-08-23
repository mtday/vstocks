package vstocks.rest.resource.user.portfolio;

import vstocks.db.ServiceFactory;
import vstocks.model.User;
import vstocks.model.UserCredits;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/user/portfolio/credits")
@Singleton
public class GetCreditBalance extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetCreditBalance(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public UserCredits getCreditBalance(@Context SecurityContext securityContext) {
        User user = getUser(securityContext);
        return serviceFactory.getUserCreditsService().get(user.getId())
                .orElseThrow(() -> new NotFoundException("Failed to find credit balance for user"));
    }
}
