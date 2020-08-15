package vstocks.rest.resource.user;

import vstocks.db.DBFactory;
import vstocks.model.User;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
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
    private final DBFactory dbFactory;

    @Inject
    public PutUser(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @PUT
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public User user(@Context SecurityContext securityContext, User user) {
        User existingUser = getUser(securityContext);
        ofNullable(user.getDisplayName()).ifPresent(existingUser::setDisplayName);
        ofNullable(user.getUsername()).ifPresent(existingUser::setUsername);
        return dbFactory.getUserDB().update(existingUser) == 1
                ? existingUser
                : dbFactory.getUserDB().get(existingUser.getId()).orElse(null); // not expecting a missing user here
    }
}
