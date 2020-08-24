package vstocks.rest.resource.dashboard;

import vstocks.db.ServiceFactory;
import vstocks.model.system.OverallCreditValueCollection;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/dashboard/overall/credits")
@Singleton
public class GetOverallCreditValue extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetOverallCreditValue(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public OverallCreditValueCollection getOverallCredits() {
        return serviceFactory.getOverallCreditValueService().getLatest();
    }
}
