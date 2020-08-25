package vstocks.rest.resource.user;

import vstocks.db.ServiceFactory;
import vstocks.model.User;
import vstocks.model.UsernameCheck;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/user/check")
@Singleton
public class CheckUsername extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public CheckUsername(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public UsernameCheck checkUsername(@Context SecurityContext securityContext,
                                       @QueryParam("username") String username) {
        User user = getUser(securityContext);
        String trimmed = ofNullable(username)
                .map(String::trim)
                .filter(u -> !u.isEmpty())
                .orElseThrow(() -> new BadRequestException("Missing or invalid username parameter"));

        boolean myUsername = trimmed.equals(user.getUsername());
        boolean exists = !myUsername && serviceFactory.getUserService().usernameExists(trimmed);
        boolean valid = myUsername || VALID_USERNAME_PATTERN.matcher(trimmed).matches();

        String message = null;
        if (exists) {
            message = USERNAME_EXISTS_MESSAGE;
        } else  if (!valid) {
            message = INVALID_USERNAME_MESSAGE;
        }

        return new UsernameCheck().setUsername(trimmed).setExists(exists).setValid(valid).setMessage(message);
    }
}
