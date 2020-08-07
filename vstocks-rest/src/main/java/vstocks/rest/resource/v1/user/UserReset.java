package vstocks.rest.resource.v1.user;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import vstocks.db.DBFactory;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/v1/user/reset")
@Singleton
public class UserReset extends BaseResource {
    private final DBFactory dbFactory;

    @Inject
    public UserReset(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @PUT
    @Pac4JSecurity(authorizers = "isAuthenticated")
    public Response reset(@Pac4JProfile CommonProfile profile) {
        dbFactory.getUserDB().reset(getUser(profile).getId());
        return Response.ok().build();
    }
}
