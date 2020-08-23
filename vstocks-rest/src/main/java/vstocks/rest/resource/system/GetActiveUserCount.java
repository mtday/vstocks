package vstocks.rest.resource.system;

import vstocks.db.ServiceFactory;
import vstocks.model.system.ActiveUserCountCollection;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/system/user-count/active")
@Singleton
public class GetActiveUserCount extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetActiveUserCount(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public ActiveUserCountCollection getActive() {
        return serviceFactory.getActiveUserCountService().getLatest();
    }
}
