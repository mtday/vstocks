package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.SortDirection.ASC;
import static vstocks.model.SortDirection.DESC;

public class DatabaseFieldTest {
    @Test
    public void testToSort() {
        Sort sort = CREDITS.toSort();
        assertEquals(CREDITS, sort.getField());
        assertEquals(ASC, sort.getDirection());
    }

    @Test
    public void testToSortWithDirection() {
        Sort sort = CREDITS.toSort(DESC);
        assertEquals(CREDITS, sort.getField());
        assertEquals(DESC, sort.getDirection());
    }

    @Test
    public void testGetField() {
        assertEquals("credits", CREDITS.getField());
        assertEquals("display_name", DISPLAY_NAME.getField());
    }
}
