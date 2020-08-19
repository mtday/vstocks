package vstocks.rest.resource.system;

import vstocks.db.DBFactory;
import vstocks.model.TransactionSummaryCollection;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/system/transaction-summary")
@Singleton
public class GetTransactionSummary extends BaseResource {
    private final DBFactory dbFactory;

    @Inject
    public GetTransactionSummary(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public TransactionSummaryCollection getTransactionSummary() {
        return dbFactory.getTransactionSummaryDB().getLatest();
    }
}
