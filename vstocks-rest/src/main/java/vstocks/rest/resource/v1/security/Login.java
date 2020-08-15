package vstocks.rest.resource.v1.security;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import vstocks.db.DBFactory;
import vstocks.model.ActivityLog;
import vstocks.model.User;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtSecurity;

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
import java.net.URLEncoder;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.temporaryRedirect;
import static vstocks.model.ActivityType.USER_LOGIN;

@Path("/v1/security/login")
@Singleton
public class Login extends BaseResource {
    private static final Random RANDOM = new Random();
    private static final String REDIRECT = "/";

    private final DBFactory dbFactory;
    private final JwtSecurity jwtSecurity;

    @Inject
    public Login(DBFactory dbFactory, JwtSecurity jwtSecurity) {
        this.dbFactory = dbFactory;
        this.jwtSecurity = jwtSecurity;
    }

    private String doLogin(CommonProfile profile) {
        Optional<User> existingUser = ofNullable(profile.getEmail())
                .map(User::generateId)
                .flatMap(id -> dbFactory.getUserDB().get(id));

        // Create the user if they don't exist
        if (existingUser.isEmpty()) {
            User profileUser = getUser(profile);
            // If the profile username already exists, update it randomly to prevent insert conflicts. The user can
            // change their username on the user profile page so this username is somewhat temporary.
            while (dbFactory.getUserDB().usernameExists(profileUser.getUsername())) {
                profileUser.setUsername(profileUser.getUsername() + (10000 + RANDOM.nextInt(89999)));
            }

            // Create the user
            dbFactory.getUserDB().add(profileUser);
            existingUser = Optional.of(profileUser);
        }

        User user = existingUser.orElseThrow(() -> new RuntimeException("Unexpected missing user"));

        // An existing user exists in the db. Update the profile image. We don't update anything else
        // (username, display name) since the user may have modified those via their profile page.
        User profileUser = getUser(profile);
        if (profileUser.getImageLink() != null && !profileUser.getImageLink().equals(user.getImageLink())) {
            user.setImageLink(profileUser.getImageLink());
            dbFactory.getUserDB().update(user);
        }

        ActivityLog activityLog = new ActivityLog()
                .setId(UUID.randomUUID().toString())
                .setUserId(user.getId())
                .setType(USER_LOGIN)
                .setTimestamp(Instant.now().truncatedTo(SECONDS));
        dbFactory.getActivityLogDB().add(activityLog);

        return jwtSecurity.generateToken(user.getId());
    }

    private Response generateResponse(UriInfo uriInfo, String token) {
        URI redirectUri = UriBuilder.fromUri(uriInfo.getRequestUri())
                .replacePath(REDIRECT)
                .queryParam("token", URLEncoder.encode(token, UTF_8))
                .build();
        return temporaryRedirect(redirectUri).header(AUTHORIZATION, "Bearer " + token).build();
    }

    @GET
    @Path("/twitter")
    @Produces(APPLICATION_JSON)
    @Pac4JSecurity(clients = "TwitterClient", authorizers = "isAuthenticated")
    public Response twitterLogin(@Context UriInfo uriInfo, @Pac4JProfile CommonProfile profile) {
        return generateResponse(uriInfo, doLogin(profile));
    }

    @GET
    @Path("/google")
    @Produces(APPLICATION_JSON)
    @Pac4JSecurity(clients = "Google2Client", authorizers = "isAuthenticated")
    public Response googleLogin(@Context UriInfo uriInfo, @Pac4JProfile CommonProfile profile) {
        return generateResponse(uriInfo, doLogin(profile));
    }

    @GET
    @Path("/facebook")
    @Produces(APPLICATION_JSON)
    @Pac4JSecurity(clients = "FacebookClient", authorizers = "isAuthenticated")
    public Response facebookLogin(@Context UriInfo uriInfo, @Pac4JProfile CommonProfile profile) {
        return generateResponse(uriInfo, doLogin(profile));
    }
}
