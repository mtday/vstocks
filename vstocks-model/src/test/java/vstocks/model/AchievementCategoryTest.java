package vstocks.model;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static vstocks.model.AchievementCategory.BEGINNER;

public class AchievementCategoryTest {
    @Test
    public void testFromName() {
        Optional<AchievementCategory> category = AchievementCategory.from(BEGINNER.name());
        assertTrue(category.isPresent());
        assertEquals(BEGINNER, category.get());
    }

    @Test
    public void testFromDisplayName() {
        Optional<AchievementCategory> category = AchievementCategory.from(BEGINNER.getDisplayName());
        assertTrue(category.isPresent());
        assertEquals(BEGINNER, category.get());
    }

    @Test
    public void testFromMissing() {
        assertFalse(AchievementCategory.from("missing").isPresent());
    }
}
