package vstocks.rest.resource.v1.security;

import org.pac4j.jax.rs.annotations.Pac4JCallback;
import vstocks.rest.resource.BaseResource;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/v1/security/callback")
@Singleton
public class Callback extends BaseResource {
    @GET
    @Pac4JCallback
    public void callback() {
        // nothing to do
    }
}
