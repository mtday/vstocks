package vstocks.rest.resource.security;

import org.pac4j.jax.rs.annotations.Pac4JLogout;
import vstocks.rest.resource.BaseResource;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/security/logout")
@Singleton
public class Logout extends BaseResource {
    @GET
    @Pac4JLogout
    public void logout() {
        // nothing to do
    }
}
