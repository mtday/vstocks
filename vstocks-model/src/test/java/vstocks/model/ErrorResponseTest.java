package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ErrorResponseTest {
    @Test
    public void testGettersAndSetters() {
        ErrorResponse errorResponse = new ErrorResponse().setStatus(404).setMessage("error message");

        assertEquals(404, errorResponse.getStatus());
        assertEquals("error message", errorResponse.getMessage());
    }

    @Test
    public void testEquals() {
        ErrorResponse errorResponse1 = new ErrorResponse().setStatus(404).setMessage("error message");
        ErrorResponse errorResponse2 = new ErrorResponse().setStatus(404).setMessage("error message");
        assertEquals(errorResponse1, errorResponse2);
    }

    @Test
    public void testHashCode() {
        ErrorResponse errorResponse = new ErrorResponse().setStatus(404).setMessage("error message");
        assertEquals(-66643300, errorResponse.hashCode());
    }

    @Test
    public void testToString() {
        ErrorResponse errorResponse = new ErrorResponse().setStatus(404).setMessage("error message");
        assertEquals("ErrorResponse{status=404, message='error message'}", errorResponse.toString());
    }
}
