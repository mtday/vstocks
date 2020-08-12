package vstocks.rest.resource.v1.user.achievement;

import vstocks.db.DBFactory;
import vstocks.model.UserAchievement;
import vstocks.rest.resource.BaseResource;
import vstocks.rest.security.JwtTokenRequired;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/user/achievements")
@Singleton
public class GetUserAchievements extends BaseResource {
    private final DBFactory dbFactory;

    @Inject
    public GetUserAchievements(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public List<UserAchievement> getUserAchievements(@Context SecurityContext securityContext) {
        return dbFactory.getUserAchievementDB().getForUser(getUser(securityContext).getId());
    }
}
