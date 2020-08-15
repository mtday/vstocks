package vstocks.rest.resource.security;

import org.pac4j.jax.rs.annotations.Pac4JCallback;
import vstocks.rest.resource.BaseResource;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/security/callback")
@Singleton
public class Callback extends BaseResource {
    @GET
    @Pac4JCallback
    public void callback() {
        // nothing to do
    }
}
