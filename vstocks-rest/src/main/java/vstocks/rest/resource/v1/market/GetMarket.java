package vstocks.rest.resource.v1.market;

import vstocks.model.Market;
import vstocks.rest.resource.BaseResource;
import vstocks.service.ServiceFactory;

import javax.inject.Inject;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/market/{id}")
public class GetMarket extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetMarket(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Market getMarket(@PathParam("id") String id) {
        return serviceFactory.getMarketService().get(id)
                .orElseThrow(() -> new NotFoundException("Market " + id + " not found"));
    }
}
