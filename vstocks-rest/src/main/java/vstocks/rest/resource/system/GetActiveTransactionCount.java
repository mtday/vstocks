package vstocks.rest.resource.system;

import vstocks.db.ServiceFactory;
import vstocks.model.system.ActiveTransactionCountCollection;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/system/transaction-count/active")
@Singleton
public class GetActiveTransactionCount extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetActiveTransactionCount(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public ActiveTransactionCountCollection getActive() {
        return serviceFactory.getActiveTransactionCountService().getLatest();
    }
}
