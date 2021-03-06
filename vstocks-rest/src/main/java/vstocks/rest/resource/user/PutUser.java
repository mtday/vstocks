package vstocks.rest.resource.user;

import vstocks.db.ServiceFactory;
import vstocks.model.User;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/user")
@Singleton
public class PutUser extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public PutUser(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @PUT
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public User user(@Context SecurityContext securityContext, User user) {
        User existingUser = getUser(securityContext);
        ofNullable(user.getDisplayName()).ifPresent(displayName -> {
            // If the display name has not been changed, let it through. This is to prevent display names received
            // from our login providers from being considered invalid.
            if (!displayName.equals(existingUser.getDisplayName())) {
                if (!VALID_DISPLAY_NAME_PATTERN.matcher(displayName).matches()) {
                    throw new BadRequestException(INVALID_DISPLAY_NAME_MESSAGE);
                }
                existingUser.setDisplayName(displayName);
            }
        });
        ofNullable(user.getUsername()).ifPresent(username -> {
            // If the username has not been changed, let it through. This is to prevent usernames received
            // from our login providers from being considered invalid.
            if (!username.equals(existingUser.getUsername())) {
                if (!VALID_USERNAME_PATTERN.matcher(username).matches()) {
                    throw new BadRequestException(INVALID_USERNAME_MESSAGE);
                }
                existingUser.setUsername(username);
            }
        });

        return serviceFactory.getUserService().update(existingUser) == 1
                ? existingUser
                : serviceFactory.getUserService().get(existingUser.getId()).orElse(null); // not expecting a missing user here
    }
}
