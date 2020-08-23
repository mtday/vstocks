package vstocks.rest.resource.user;

import vstocks.db.ServiceFactory;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/user/exists")
@Singleton
public class UsernameExists extends BaseResource {
    private final ServiceFactory dbFactory;

    @Inject
    public UsernameExists(ServiceFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public vstocks.model.UsernameExists usernameExists(@QueryParam("username") String username) {
        String trimmed = ofNullable(username)
                .map(String::trim)
                .filter(u -> !u.isEmpty())
                .orElseThrow(() -> new BadRequestException("Missing or invalid username parameter"));
        return new vstocks.model.UsernameExists()
                .setUsername(trimmed)
                .setExists(dbFactory.getUserService().usernameExists(trimmed));
    }
}
