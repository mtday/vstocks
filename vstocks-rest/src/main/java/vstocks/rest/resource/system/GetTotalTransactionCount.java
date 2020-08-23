package vstocks.rest.resource.system;

import vstocks.db.ServiceFactory;
import vstocks.model.system.TotalTransactionCountCollection;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/system/transaction-count/total")
@Singleton
public class GetTotalTransactionCount extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetTotalTransactionCount(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public TotalTransactionCountCollection getTotal() {
        return serviceFactory.getTotalTransactionCountService().getLatest();
    }
}
