package vstocks.rest.resource.system;

import vstocks.db.DBFactory;
import vstocks.model.UserCount;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/system/user-count")
@Singleton
public class GetUserCount extends BaseResource {
    private final DBFactory dbFactory;

    @Inject
    public GetUserCount(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public UserCount getValueSummary() {
        return dbFactory.getUserCountDB().getLatest();
    }
}
