package vstocks.rest.resource.v1.user.achievement;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import vstocks.db.DBFactory;
import vstocks.model.UserAchievement;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    @Pac4JSecurity(authorizers = "isAuthenticated")
    public List<UserAchievement> getUserAchievements(@Pac4JProfile CommonProfile profile) {
        return dbFactory.getUserAchievementDB().getForUser(getUser(profile).getId());
    }
}
