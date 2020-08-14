package vstocks.rest.security;

import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;

public class JwtSecurityTest {
    @Test
    public void testCreateKey() {
        assertNotNull(JwtSecurity.createKey("test"));
    }

    @Test
    public void testGetClaimsSet() {
        JWTClaimsSet claims = JwtSecurity.getClaimsSet("userId");
        assertEquals("userId", claims.getSubject());
        assertEquals("[vstocks]", claims.getAudience().toString());
        assertEquals("vstocks", claims.getIssuer());
        assertNotNull(claims.getIssueTime());
        assertNotNull(claims.getExpirationTime());
    }

    @Test
    public void testGetUserIdNull() {
        assertFalse(JwtSecurity.getUserId(null).isPresent());
    }

    @Test
    public void testGetUserIdNullSubject() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .audience("vstocks")
                .issuer("vstocks")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plusSeconds(10).toEpochMilli()))
                .build();
        assertFalse(JwtSecurity.getUserId(claims).isPresent());
    }

    @Test
    public void testGetUserIdNullIssuer() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("userId")
                .audience("vstocks")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plusSeconds(10).toEpochMilli()))
                .build();
        assertFalse(JwtSecurity.getUserId(claims).isPresent());
    }

    @Test
    public void testGetUserIdInvalidIssuer() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("userId")
                .audience("vstocks")
                .issuer("invalid")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plusSeconds(10).toEpochMilli()))
                .build();
        assertFalse(JwtSecurity.getUserId(claims).isPresent());
    }

    @Test
    public void testGetUserIdNullAudience() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("userId")
                .issuer("vstocks")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plusSeconds(10).toEpochMilli()))
                .build();
        assertFalse(JwtSecurity.getUserId(claims).isPresent());
    }

    @Test
    public void testGetUserIdEmptyAudience() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("userId")
                .audience(emptyList())
                .issuer("vstocks")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plusSeconds(10).toEpochMilli()))
                .build();
        assertFalse(JwtSecurity.getUserId(claims).isPresent());
    }

    @Test
    public void testGetUserIdInvalidAudience() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("userId")
                .audience("invalid")
                .issuer("vstocks")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plusSeconds(10).toEpochMilli()))
                .build();
        assertFalse(JwtSecurity.getUserId(claims).isPresent());
    }

    @Test
    public void testGetUserIdNullExpiration() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("userId")
                .audience("vstocks")
                .issuer("vstocks")
                .issueTime(new Date())
                .build();
        assertFalse(JwtSecurity.getUserId(claims).isPresent());
    }

    @Test
    public void testGetUserIdExpired() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("userId")
                .audience("vstocks")
                .issuer("vstocks")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().minusSeconds(10).toEpochMilli()))
                .build();
        assertFalse(JwtSecurity.getUserId(claims).isPresent());
    }

    @Test
    public void testGetUserIdValid() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("userId")
                .audience("vstocks")
                .issuer("vstocks")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plusSeconds(10).toEpochMilli()))
                .build();
        assertEquals("userId", JwtSecurity.getUserId(claims).orElse(null));
    }

    @Test
    public void testRoundTrip() {
        JwtSecurity jwtSecurity = new JwtSecurity();
        String token = jwtSecurity.generateToken("userId");
        Optional<String> userId = jwtSecurity.validateToken(token);
        assertEquals("userId", userId.orElse(null));
    }
}
