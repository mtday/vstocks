package vstocks.rest.resource.system;

import vstocks.db.DBFactory;
import vstocks.model.PortfolioValueSummary;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/system/value-summary")
@Singleton
public class GetValueSummary extends BaseResource {
    private final DBFactory dbFactory;

    @Inject
    public GetValueSummary(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public PortfolioValueSummary getValueSummary() {
        return dbFactory.getPortfolioValueSummaryDB().getLatest();
    }
}
