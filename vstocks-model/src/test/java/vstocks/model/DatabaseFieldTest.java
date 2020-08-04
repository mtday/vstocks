package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static vstocks.model.DatabaseField.BALANCE;
import static vstocks.model.DatabaseField.DISPLAY_NAME;
import static vstocks.model.Sort.SortDirection.ASC;
import static vstocks.model.Sort.SortDirection.DESC;

public class DatabaseFieldTest {
    @Test
    public void testToSort() {
        Sort sort = BALANCE.toSort();
        assertEquals(BALANCE, sort.getField());
        assertEquals(ASC, sort.getDirection());
    }

    @Test
    public void testToSortWithDirection() {
        Sort sort = BALANCE.toSort(DESC);
        assertEquals(BALANCE, sort.getField());
        assertEquals(DESC, sort.getDirection());
    }

    @Test
    public void testGetField() {
        assertEquals("balance", BALANCE.getField());
        assertEquals("display_name", DISPLAY_NAME.getField());
    }
}
