package vstocks.rest.security;

import org.junit.Test;
import vstocks.model.User;

import static org.junit.Assert.*;
import static vstocks.model.User.generateId;

public class UserSecurityContextTest {
    @Test
    public void test() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("user").setDisplayName("User");
        UserSecurityContext userSecurityContext = new UserSecurityContext(user);

        assertEquals(user, userSecurityContext.getUserPrincipal());
        assertFalse(userSecurityContext.isUserInRole("anything"));
        assertTrue(userSecurityContext.isSecure());
        assertEquals("user", userSecurityContext.getAuthenticationScheme());
    }
}
