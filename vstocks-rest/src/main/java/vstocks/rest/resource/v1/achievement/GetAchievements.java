package vstocks.rest.resource.v1.achievement;

import vstocks.achievement.AchievementService;
import vstocks.model.Achievement;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/achievements")
@Singleton
public class GetAchievements extends BaseResource {
    private final AchievementService achievementService;

    @Inject
    public GetAchievements(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Set<Achievement> getAchievements() {
        return achievementService.getAchievements();
    }
}
