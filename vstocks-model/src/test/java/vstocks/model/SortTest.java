package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.CREDITS;
import static vstocks.model.SortDirection.ASC;
import static vstocks.model.SortDirection.DESC;

public class SortTest {
    @Test
    public void testDefaultConstructor() {
        Sort sort = new Sort();

        assertNull(sort.getField());
        assertEquals(ASC, sort.getDirection());
    }

    @Test
    public void testConstructorWithField() {
        Sort sort = new Sort(CREDITS);

        assertEquals(CREDITS, sort.getField());
        assertEquals(ASC, sort.getDirection());
    }

    @Test
    public void testConstructorWithFieldAndDirection() {
        Sort sort = new Sort(CREDITS, DESC);

        assertEquals(CREDITS, sort.getField());
        assertEquals(DESC, sort.getDirection());
    }

    @Test
    public void testParseNoDirection() {
        Sort sort = Sort.parse("CREDITS");
        assertEquals(CREDITS, sort.getField());
        assertEquals(ASC, sort.getDirection());
    }

    @Test
    public void testParseWithDirection() {
        Sort sort = Sort.parse("CREDITS:DESC");
        assertEquals(CREDITS, sort.getField());
        assertEquals(DESC, sort.getDirection());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalid() {
        Sort.parse("MISSING");
    }

    @Test
    public void testGettersAndSetters() {
        Sort sort = new Sort().setField(CREDITS).setDirection(DESC);

        assertEquals(CREDITS, sort.getField());
        assertEquals(DESC, sort.getDirection());
    }

    @Test
    public void testEquals() {
        Sort sort1 = new Sort().setField(CREDITS).setDirection(DESC);
        Sort sort2 = new Sort().setField(CREDITS).setDirection(DESC);
        assertEquals(sort1, sort2);
    }

    @Test
    public void testHashCode() {
        Sort sort = new Sort().setField(CREDITS).setDirection(DESC);
        assertNotEquals(0, new Sort().hashCode()); // enums make the value inconsistent
        assertNotEquals(0, sort.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        Sort sort = new Sort().setField(CREDITS).setDirection(DESC);
        assertEquals("credits DESC", sort.toString());
    }
}
