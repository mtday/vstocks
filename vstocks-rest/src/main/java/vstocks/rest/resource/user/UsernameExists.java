package vstocks.rest.resource.user;

import vstocks.db.DBFactory;
import vstocks.model.rest.UsernameExistsResponse;
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
    private final DBFactory dbFactory;

    @Inject
    public UsernameExists(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public UsernameExistsResponse usernameExists(@QueryParam("username") String username) {
        String trimmed = ofNullable(username)
                .map(String::trim)
                .filter(u -> !u.isEmpty())
                .orElseThrow(() -> new BadRequestException("Missing or invalid username parameter"));
        return new UsernameExistsResponse()
                .setUsername(trimmed)
                .setExists(dbFactory.getUserDB().usernameExists(trimmed));
    }
}
