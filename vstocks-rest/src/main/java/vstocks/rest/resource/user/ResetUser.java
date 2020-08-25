package vstocks.rest.resource.user;

import vstocks.db.ServiceFactory;
import vstocks.model.User;
import vstocks.model.UserReset;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
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
    public UserReset reset(@Context SecurityContext securityContext) {
        User user = getUser(securityContext);
        return new UserReset()
                .setUser(user)
                .setReset(serviceFactory.getUserService().reset(user.getId()) == 1);
    }
}
