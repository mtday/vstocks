package vstocks.rest.resource.standings;

import vstocks.db.DBFactory;
import vstocks.model.PortfolioValueRank;
import vstocks.model.Results;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/standings/ranks")
@Singleton
public class GetRanks extends BaseResource {
    private final DBFactory dbFactory;

    @Inject
    public GetRanks(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Results<PortfolioValueRank> getStocks(@QueryParam("pageNum") Integer pageNum,
                                                 @QueryParam("pageSize") Integer pageSize,
                                                 @QueryParam("sort") String sort) {
        return dbFactory.getPortfolioValueRankDB().getAll(getPage(pageNum, pageSize), getSort(sort));
    }
}
