package vstocks.rest.resource.v1.user;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import vstocks.model.User;
import vstocks.rest.resource.BaseResource;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static vstocks.model.UserSource.TWITTER;

@Path("/v1/user")
@Singleton
public class GetUser extends BaseResource {
    @GET
    @Produces(APPLICATION_JSON)
    @Pac4JSecurity(authorizers = "isAuthenticated")
    public User getUser(@Pac4JProfile CommonProfile profile) {
        return new User()
                .setId(profile.getId())
                .setUsername(profile.getUsername())
                .setSource(TWITTER);
    }
}
