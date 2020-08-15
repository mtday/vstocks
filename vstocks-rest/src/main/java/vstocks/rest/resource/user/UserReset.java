package vstocks.rest.resource.user;

import vstocks.db.DBFactory;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/user/reset")
@Singleton
public class UserReset extends BaseResource {
    private final DBFactory dbFactory;

    @Inject
    public UserReset(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @PUT
    @JwtTokenRequired
    public Response reset(@Context SecurityContext securityContext) {
        dbFactory.getUserDB().reset(getUser(securityContext).getId());
        return Response.ok().build();
    }
}
