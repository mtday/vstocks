package vstocks.rest.resource.system;

import vstocks.db.ServiceFactory;
import vstocks.model.system.TotalUserCountCollection;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/system/user-count/total")
@Singleton
public class GetTotalUserCount extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetTotalUserCount(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public TotalUserCountCollection getTotal() {
        return serviceFactory.getTotalUserCountService().getLatest();
    }
}
