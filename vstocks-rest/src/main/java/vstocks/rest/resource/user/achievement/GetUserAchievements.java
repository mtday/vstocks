package vstocks.rest.resource.user.achievement;

import vstocks.db.ServiceFactory;
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

@Path("/user/achievements")
@Singleton
public class GetUserAchievements extends BaseResource {
    private final ServiceFactory dbFactory;

    @Inject
    public GetUserAchievements(ServiceFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @JwtTokenRequired
    public List<UserAchievement> getUserAchievements(@Context SecurityContext securityContext) {
        return dbFactory.getUserAchievementDB().getForUser(getUser(securityContext).getId());
    }
}
