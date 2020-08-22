package vstocks.rest.resource.system;

import vstocks.db.ServiceFactory;
import vstocks.model.system.UserCountCollection;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/system/user-count/total")
@Singleton
public class GetUserCountTotal extends BaseResource {
    private final ServiceFactory dbFactory;

    @Inject
    public GetUserCountTotal(ServiceFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public UserCountCollection getTotal() {
        return dbFactory.getUserCountDB().getLatestTotal();
    }
}
