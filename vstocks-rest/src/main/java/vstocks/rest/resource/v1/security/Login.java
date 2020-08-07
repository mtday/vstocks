package vstocks.rest.resource.v1.security;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import vstocks.db.DBFactory;
import vstocks.model.User;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;
import java.util.Random;

import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/security/login")
@Singleton
public class Login extends BaseResource {
    private static final Random RANDOM = new Random();
    private static final String REDIRECT = "/app/index.html";

    private final DBFactory dbFactory;

    @Inject
    public Login(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    private void doLogin(CommonProfile profile) {
        Optional<User> existingUser = ofNullable(profile.getEmail())
                .map(User::generateId)
                .flatMap(id -> dbFactory.getUserDB().get(id));

        // Create the user if they don't exist
        if (existingUser.isEmpty()) {
            User profileUser = getUser(profile);
            // If the profile username already exists, update it randomly to prevent insert conflicts. The user can
            // change their username on the user profile page so this username is somewhat temporary.
            if (dbFactory.getUserDB().usernameExists(profileUser.getUsername())) {
                profileUser.setUsername(profileUser.getUsername() + (10000 + RANDOM.nextInt(89999)));
            }

            // Create the user
            dbFactory.getUserDB().add(profileUser);
        }
    }

    @GET
    @Path("/twitter")
    @Produces(APPLICATION_JSON)
    @Pac4JSecurity(clients = "TwitterClient", authorizers = "isAuthenticated")
    public Response twitterLogin(@Context UriInfo uriInfo, @Pac4JProfile CommonProfile profile) {
        doLogin(profile);
        URI redirectUri = UriBuilder.fromUri(uriInfo.getRequestUri()).replacePath(REDIRECT).build();
        return Response.temporaryRedirect(redirectUri).build();
    }
}
