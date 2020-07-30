package vstocks.rest.resource.v1.security;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import vstocks.rest.resource.BaseResource;
import vstocks.service.db.DatabaseServiceFactory;

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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/security/login")
@Singleton
public class Login extends BaseResource {
    private final DatabaseServiceFactory databaseServiceFactory;

    @Inject
    public Login(DatabaseServiceFactory databaseServiceFactory) {
        this.databaseServiceFactory = databaseServiceFactory;
    }

    @GET
    @Path("/twitter")
    @Produces(APPLICATION_JSON)
    @Pac4JSecurity(clients = "TwitterClient", authorizers = "isAuthenticated")
    public Response twitterLogin(@Context UriInfo uriInfo, @Pac4JProfile CommonProfile profile) {
        databaseServiceFactory.getUserService().login(getUser(profile));

        URI redirectUri = UriBuilder.fromUri(uriInfo.getRequestUri()).replacePath("/app/index.html").build();
        return Response.temporaryRedirect(redirectUri).build();
    }
}
