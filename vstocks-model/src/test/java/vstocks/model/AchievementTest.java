package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static vstocks.model.AchievementCategory.BEGINNER;

public class AchievementTest {
    @Test
    public void testGettersAndSettersAll() {
        Achievement achievement = new Achievement()
                .setId("id")
                .setName("name")
                .setCategory(BEGINNER)
                .setDescription("description")
                .setOrder(10);

        assertEquals("id", achievement.getId());
        assertEquals("name", achievement.getName());
        assertEquals(BEGINNER, achievement.getCategory());
        assertEquals("description", achievement.getDescription());
        assertEquals(10, achievement.getOrder());
    }

    @Test
    public void testEquals() {
        Achievement achievement1 = new Achievement()
                .setId("id")
                .setName("name")
                .setCategory(BEGINNER)
                .setDescription("description")
                .setOrder(10);
        Achievement achievement2 = new Achievement()
                .setId("id")
                .setName("name")
                .setCategory(BEGINNER)
                .setDescription("description")
                .setOrder(10);
        assertEquals(achievement1, achievement2);
    }

    @Test
    public void testHashCode() {
        Achievement achievement = new Achievement()
                .setId("id")
                .setName("name")
                .setCategory(BEGINNER)
                .setDescription("description")
                .setOrder(10);
        assertEquals(243300655, achievement.hashCode());
    }

    @Test
    public void testToString() {
        Achievement achievement = new Achievement()
                .setId("id")
                .setName("name")
                .setCategory(BEGINNER)
                .setDescription("description")
                .setOrder(10);
        assertEquals("Achievement{id='id', name='name', category=Beginner, description='description', order=10}",
                achievement.toString());
    }
}
