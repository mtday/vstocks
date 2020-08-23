package vstocks.rest.resource.user;

import vstocks.db.ServiceFactory;
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
public class ResetUser extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public ResetUser(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @PUT
    @JwtTokenRequired
    public Response reset(@Context SecurityContext securityContext) {
        serviceFactory.getUserService().reset(getUser(securityContext).getId());
        return Response.ok().build();
    }
}
