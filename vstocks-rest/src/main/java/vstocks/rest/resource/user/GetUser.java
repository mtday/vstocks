package vstocks.rest.resource.user;

import vstocks.model.User;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/user")
@Singleton
public class GetUser extends BaseResource {
    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public User user(@Context SecurityContext securityContext) {
        return getUser(securityContext);
    }
}
