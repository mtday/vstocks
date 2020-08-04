package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static vstocks.model.DatabaseField.BALANCE;
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
        Sort sort = new Sort(BALANCE);

        assertEquals(BALANCE, sort.getField());
        assertEquals(ASC, sort.getDirection());
    }

    @Test
    public void testConstructorWithFieldAndDirection() {
        Sort sort = new Sort(BALANCE, DESC);

        assertEquals(BALANCE, sort.getField());
        assertEquals(DESC, sort.getDirection());
    }

    @Test
    public void testParseNoDirection() {
        Sort sort = Sort.parse("BALANCE");
        assertEquals(BALANCE, sort.getField());
        assertEquals(ASC, sort.getDirection());
    }

    @Test
    public void testParseWithDirection() {
        Sort sort = Sort.parse("BALANCE:DESC");
        assertEquals(BALANCE, sort.getField());
        assertEquals(DESC, sort.getDirection());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalid() {
        Sort.parse("MISSING");
    }

    @Test
    public void testGettersAndSetters() {
        Sort sort = new Sort().setField(BALANCE).setDirection(DESC);

        assertEquals(BALANCE, sort.getField());
        assertEquals(DESC, sort.getDirection());
    }

    @Test
    public void testEquals() {
        Sort sort1 = new Sort().setField(BALANCE).setDirection(DESC);
        Sort sort2 = new Sort().setField(BALANCE).setDirection(DESC);
        assertEquals(sort1, sort2);
    }

    @Test
    public void testHashCode() {
        Sort sort = new Sort().setField(BALANCE).setDirection(DESC);
        assertEquals(-1140107498, sort.hashCode());
    }

    @Test
    public void testToString() {
        Sort sort = new Sort().setField(BALANCE).setDirection(DESC);
        assertEquals("balance DESC", sort.toString());
    }
}
