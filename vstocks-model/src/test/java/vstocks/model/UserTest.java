package vstocks.model;

import org.junit.Test;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.*;

public class UserTest {
    @Test
    public void testGettersAndSetters() {
        User user = new User().setEmail("user@domain.com").setUsername("username").setDisplayName("displayName");

        assertEquals("cd2bfcff-e5fe-34a1-949d-101994d0987f", user.getId()); // automatically set via User#setEmail
        assertEquals("user@domain.com", user.getEmail());
        assertEquals("username", user.getName());
        assertEquals("username", user.getUsername());
        assertEquals("displayName", user.getDisplayName());
    }

    @Test
    public void testGetJwtClaims() {
        User user = new User().setEmail("user@domain.com").setUsername("username").setDisplayName("displayName");
        Map<String, Object> claims = user.getJwtClaims();

        assertEquals(4, claims.size());
        assertEquals("cd2bfcff-e5fe-34a1-949d-101994d0987f", claims.get("id"));
        assertEquals("user@domain.com", claims.get("email"));
        assertEquals("username", claims.get("username"));
        assertEquals("displayName", claims.get("displayName"));
    }

    @Test
    public void testGetUserFromJwtClaimsNull() {
        assertFalse(User.getUserFromJwtClaims(null).isPresent());
    }

    @Test
    public void testGetUserFromJwtClaimsEmptyMap() {
        assertFalse(User.getUserFromJwtClaims(emptyMap()).isPresent());
    }

    @Test
    public void testGetUserFromJwtClaimsMissingId() {
        User user = new User().setEmail("user@domain.com").setUsername("username").setDisplayName("displayName");
        Map<String, Object> claims = user.getJwtClaims();
        claims.remove("id");
        assertFalse(User.getUserFromJwtClaims(claims).isPresent());
    }

    @Test
    public void testGetUserFromJwtClaimsMissingEmail() {
        User user = new User().setEmail("user@domain.com").setUsername("username").setDisplayName("displayName");
        Map<String, Object> claims = user.getJwtClaims();
        claims.remove("email");
        assertFalse(User.getUserFromJwtClaims(claims).isPresent());
    }

    @Test
    public void testGetUserFromJwtClaimsMissingUsername() {
        User user = new User().setEmail("user@domain.com").setUsername("username").setDisplayName("displayName");
        Map<String, Object> claims = user.getJwtClaims();
        claims.remove("username");
        assertFalse(User.getUserFromJwtClaims(claims).isPresent());
    }

    @Test
    public void testGetUserFromJwtClaimsMissingDisplayName() {
        User user = new User().setEmail("user@domain.com").setUsername("username").setDisplayName("displayName");
        Map<String, Object> claims = user.getJwtClaims();
        claims.remove("displayName");
        assertFalse(User.getUserFromJwtClaims(claims).isPresent());
    }

    @Test
    public void testGetUserFromJwtClaimsWrongId() {
        User user = new User().setEmail("user@domain.com").setUsername("username").setDisplayName("displayName");
        Map<String, Object> claims = user.getJwtClaims();
        claims.put("id", "wrong");
        assertFalse(User.getUserFromJwtClaims(claims).isPresent());
    }

    @Test
    public void testGetUserFromJwtClaimsValid() {
        User user = new User().setEmail("user@domain.com").setUsername("username").setDisplayName("displayName");
        Map<String, Object> claims = user.getJwtClaims();
        Optional<User> optional = User.getUserFromJwtClaims(claims);
        assertTrue(optional.isPresent());
        assertEquals(user.getId(), optional.get().getId());
        assertEquals(user.getEmail(), optional.get().getEmail());
        assertEquals(user.getUsername(), optional.get().getUsername());
        assertEquals(user.getDisplayName(), optional.get().getDisplayName());
    }

    @Test
    public void testEquals() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        User user1 = new User().setId(userId).setUsername("username1");
        User user2 = new User().setId(userId).setUsername("username2");
        assertEquals(user1, user2);
    }

    @Test
    public void testHashCode() {
        User user = new User().setEmail("user@domain.com").setUsername("username").setDisplayName("displayName");
        assertEquals(2060421285, user.hashCode());
    }

    @Test
    public void testToString() {
        User user = new User().setEmail("user@domain.com").setUsername("username").setDisplayName("displayName");
        assertEquals("User{id='cd2bfcff-e5fe-34a1-949d-101994d0987f', email='user@domain.com', username='username', "
                + "displayName='displayName'}", user.toString());
    }
}
