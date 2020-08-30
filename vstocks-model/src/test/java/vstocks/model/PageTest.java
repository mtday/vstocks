package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class PageTest {
    @Test
    public void testGettersAndSetters() {
        // [1     p1    10]
        // [11    p2    20]
        // [21    p3    30]
        // [31    p4    40]
        // [41  p5  46]
        Page page = new Page()
                .setPage(3)
                .setSize(10)
                .setTotalPages(5)
                .setFirstRow(31)
                .setLastRow(40)
                .setTotalRows(46);

        assertEquals(3, (int) page.getPage());
        assertEquals(10, (int) page.getSize());
        assertEquals(5, (int) page.getTotalPages());
        assertEquals(31, (int) page.getFirstRow());
        assertEquals(40, (int) page.getLastRow());
        assertEquals(46, (int) page.getTotalRows());
    }

    @Test
    public void testFromNoResults() {
        // [1     p1    10]
        // [11    p2    20]
        // [21    p3    30]
        // [31    p4    40]
        // [41  p5  46]
        Page page = Page.from(1, 10, 0, 0);
        assertEquals(1, (int) page.getPage());
        assertEquals(10, (int) page.getSize());
        assertEquals(0, (int) page.getTotalPages());
        assertNull(page.getFirstRow());
        assertNull(page.getLastRow());
        assertEquals(0, (int) page.getTotalRows());
    }

    @Test
    public void testFromFirst() {
        // [1     p1    10]
        // [11    p2    20]
        // [21    p3    30]
        // [31    p4    40]
        // [41  p5  46]
        Page page = Page.from(1, 10, 10, 46);
        assertEquals(1, (int) page.getPage());
        assertEquals(10, (int) page.getSize());
        assertEquals(5, (int) page.getTotalPages());
        assertEquals(1, (int) page.getFirstRow());
        assertEquals(10, (int) page.getLastRow());
        assertEquals(46, (int) page.getTotalRows());
    }

    @Test
    public void testFromMiddle() {
        // [1     p1    10]
        // [11    p2    20]
        // [21    p3    30]
        // [31    p4    40]
        // [41  p5  46]
        Page page = Page.from(3, 10, 10, 46);
        assertEquals(3, (int) page.getPage());
        assertEquals(10, (int) page.getSize());
        assertEquals(5, (int) page.getTotalPages());
        assertEquals(21, (int) page.getFirstRow());
        assertEquals(30, (int) page.getLastRow());
        assertEquals(46, (int) page.getTotalRows());
    }

    @Test
    public void testFromLastPage() {
        // [1     p1    10]
        // [11    p2    20]
        // [21    p3    30]
        // [31    p4    40]
        // [41  p5  46]
        Page page = Page.from(5, 10, 6, 46);
        assertEquals(5, (int) page.getPage());
        assertEquals(10, (int) page.getSize());
        assertEquals(5, (int) page.getTotalPages());
        assertEquals(41, (int) page.getFirstRow());
        assertEquals(46, (int) page.getLastRow());
        assertEquals(46, (int) page.getTotalRows());
    }

    @Test
    public void testPreviousFromEmptyPage() {
        Page page = Page.from(1, 10, 0, 0);
        assertFalse(page.previous().isPresent());
    }

    @Test
    public void testPreviousFromFirst() {
        // [1     p1    10]
        // [11    p2    20]
        // [21    p3    30]
        // [31    p4    40]
        // [41  p5  46]
        Page page = Page.from(1, 10, 10, 46);
        assertFalse(page.previous().isPresent());
    }

    @Test
    public void testPreviousFromMiddle() {
        // [1     p1    10]
        // [11    p2    20]
        // [21    p3    30]
        // [31    p4    40]
        // [41  p5  46]
        Page page = Page.from(3, 10, 10, 46);
        Page previous = page.previous().orElse(null);
        assertNotNull(previous);
        assertEquals(2, (int) previous.getPage());
        assertEquals(10, (int) previous.getSize());
        assertEquals(5, (int) previous.getTotalPages());
        assertEquals(11, (int) previous.getFirstRow());
        assertEquals(20, (int) previous.getLastRow());
        assertEquals(46, (int) previous.getTotalRows());
    }

    @Test
    public void testPreviousFromLast() {
        // [1     p1    10]
        // [11    p2    20]
        // [21    p3    30]
        // [31    p4    40]
        // [41  p5  46]
        Page page = Page.from(5, 10, 6, 46);
        Page previous = page.previous().orElse(null);
        assertNotNull(previous);
        assertEquals(4, (int) previous.getPage());
        assertEquals(10, (int) previous.getSize());
        assertEquals(5, (int) previous.getTotalPages());
        assertEquals(31, (int) previous.getFirstRow());
        assertEquals(40, (int) previous.getLastRow());
        assertEquals(46, (int) previous.getTotalRows());
    }

    @Test
    public void testNextFromEmptyPage() {
        Page page = Page.from(1, 10, 0, 0);
        assertFalse(page.next().isPresent());
    }

    @Test
    public void testNextFromFirst() {
        // [1     p1    10]
        // [11    p2    20]
        // [21    p3    30]
        // [31    p4    40]
        // [41  p5  46]
        Page page = Page.from(1, 10, 10, 46);
        Page next = page.next().orElse(null);
        assertNotNull(next);
        assertEquals(2, (int) next.getPage());
        assertEquals(10, (int) next.getSize());
        assertEquals(5, (int) next.getTotalPages());
        assertEquals(11, (int) next.getFirstRow());
        assertEquals(20, (int) next.getLastRow());
        assertEquals(46, (int) next.getTotalRows());
    }

    @Test
    public void testNextFromMiddle() {
        // [1     p1    10]
        // [11    p2    20]
        // [21    p3    30]
        // [31    p4    40]
        // [41  p5  46]
        Page page = Page.from(3, 10, 10, 46);
        Page next = page.next().orElse(null);
        assertNotNull(next);
        assertEquals(4, (int) next.getPage());
        assertEquals(10, (int) next.getSize());
        assertEquals(5, (int) next.getTotalPages());
        assertEquals(31, (int) next.getFirstRow());
        assertEquals(40, (int) next.getLastRow());
        assertEquals(46, (int) next.getTotalRows());
    }

    @Test
    public void testNextFromSecondToLast() {
        // [1     p1    10]
        // [11    p2    20]
        // [21    p3    30]
        // [31    p4    40]
        // [41  p5  46]
        Page page = Page.from(4, 10, 10, 46);
        Page next = page.next().orElse(null);
        assertNotNull(next);
        assertEquals(5, (int) next.getPage());
        assertEquals(10, (int) next.getSize());
        assertEquals(5, (int) next.getTotalPages());
        assertEquals(41, (int) next.getFirstRow());
        assertEquals(46, (int) next.getLastRow());
        assertEquals(46, (int) next.getTotalRows());
    }

    @Test
    public void testNextFromLast() {
        // [1     p1    10]
        // [11    p2    20]
        // [21    p3    30]
        // [31    p4    40]
        // [41  p5  46]
        Page page = Page.from(5, 10, 6, 46);
        assertFalse(page.next().isPresent());
    }

    @Test
    public void testEquals() {
        Page page1 = Page.from(3, 10, 10, 46);
        Page page2 = Page.from(3, 10, 10, 46);
        assertEquals(page1, page2);
    }

    @Test
    public void testHashCode() {
        Page page = Page.from(3, 10, 10, 46);
        assertEquals(982796456, page.hashCode());
    }

    @Test
    public void testToString() {
        Page page = Page.from(3, 10, 10, 46);
        assertEquals("Page{page=3, size=10, totalPages=5, firstRow=21, lastRow=30, totalRows=46}", page.toString());
    }
}
