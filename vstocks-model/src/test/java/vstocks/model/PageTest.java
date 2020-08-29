package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PageTest {
    @Test
    public void testGettersAndSetters() {
        Page page = new Page().setPage(5).setSize(30);

        assertEquals(5, page.getPage());
        assertEquals(30, page.getSize());
    }

    @Test
    public void testNext() {
        Page page = new Page().setPage(5).setSize(30);
        Page next = page.next();

        assertEquals(6, next.getPage());
        assertEquals(30, next.getSize());
    }

    @Test
    public void testEquals() {
        Page page1 = new Page().setPage(5).setSize(30);
        Page page2 = new Page().setPage(5).setSize(30);
        assertEquals(page1, page2);
    }

    @Test
    public void testHashCode() {
        Page page = new Page().setPage(5).setSize(30);
        assertEquals(1146, page.hashCode());
    }

    @Test
    public void testToString() {
        Page page = new Page().setPage(5).setSize(30);
        assertEquals("Page{page=5, size=30}", page.toString());
    }
}
