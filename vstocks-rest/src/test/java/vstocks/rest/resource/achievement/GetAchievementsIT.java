package vstocks.rest.resource.achievement;

import org.junit.Test;
import vstocks.model.Achievement;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static vstocks.model.AchievementCategory.BEGINNER;

public class GetAchievementsIT extends ResourceTest {
    @Test
    public void testGetAchievementsNone() {
        when(getAchievementService().getAchievements()).thenReturn(emptySet());

        Response response = target("/achievements").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("[]", json);

        List<Achievement> achievements = convert(json, new AchievementListTypeRef());
        assertTrue(achievements.isEmpty());
    }

    @Test
    public void testGetAchievementsSome() {
        Achievement achievement = new Achievement()
                .setId("id")
                .setName("name")
                .setCategory(BEGINNER)
                .setOrder(5)
                .setDescription("description");
        when(getAchievementService().getAchievements()).thenReturn(singleton(achievement));

        Response response = target("/achievements").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("[{\"id\":\"id\",\"name\":\"name\",\"category\":\"Beginner\",\"description\":\"description\","
                + "\"order\":5}]", json);

        List<Achievement> achievements = convert(json, new AchievementListTypeRef());
        assertEquals(1, achievements.size());
        assertEquals(achievement.getId(), achievements.get(0).getId());
        assertEquals(achievement.getName(), achievements.get(0).getName());
        assertEquals(achievement.getCategory(), achievements.get(0).getCategory());
        assertEquals(achievement.getOrder(), achievements.get(0).getOrder());
        assertEquals(achievement.getDescription(), achievements.get(0).getDescription());
    }
}
