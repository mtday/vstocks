package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static vstocks.model.DatabaseField.CREDITS;
import static vstocks.model.Sort.SortDirection.ASC;
import static vstocks.model.Sort.SortDirection.DESC;

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
        assertEquals(-1687369448, sort.hashCode());
    }

    @Test
    public void testToString() {
        Sort sort = new Sort().setField(CREDITS).setDirection(DESC);
        assertEquals("credits DESC", sort.toString());
    }
}
