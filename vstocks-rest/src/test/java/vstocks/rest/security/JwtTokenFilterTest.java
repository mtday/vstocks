package vstocks.rest.security;

import org.junit.Test;
import vstocks.db.ServiceFactory;
import vstocks.db.UserService;
import vstocks.model.ErrorResponse;
import vstocks.model.User;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.Optional;

import static java.util.Optional.empty;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static vstocks.model.User.generateId;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class JwtTokenFilterTest {
    private void verifyUnauthorized(ContainerRequestContext containerRequestContext) {
        ErrorResponse errorResponse = new ErrorResponse()
                .setStatus(UNAUTHORIZED.getStatusCode())
                .setMessage(INVALID_JWT_MESSAGE);
        verify(containerRequestContext, times(1)).abortWith(argThat(response ->
                response.getStatus() == UNAUTHORIZED.getStatusCode()
                        && response.getHeaders().get(CONTENT_TYPE).contains(APPLICATION_JSON)
                        && response.hasEntity()
                        && response.getEntity().equals(errorResponse)));
    }

    @Test
    public void testNoAuthHeader() {
        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        when(containerRequestContext.getHeaderString(eq(AUTHORIZATION))).thenReturn(null);

        ServiceFactory dbFactory = mock(ServiceFactory.class);
        JwtSecurity jwtSecurity = mock(JwtSecurity.class);

        new JwtTokenFilter(dbFactory, jwtSecurity).filter(containerRequestContext);
        verifyUnauthorized(containerRequestContext);
    }

    @Test
    public void testAuthHeaderNotBearer() {
        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        when(containerRequestContext.getHeaderString(eq(AUTHORIZATION))).thenReturn("Basic user:pass");

        ServiceFactory dbFactory = mock(ServiceFactory.class);
        JwtSecurity jwtSecurity = mock(JwtSecurity.class);

        new JwtTokenFilter(dbFactory, jwtSecurity).filter(containerRequestContext);
        verifyUnauthorized(containerRequestContext);
    }

    @Test
    public void testTokenInvalid() {
        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        when(containerRequestContext.getHeaderString(eq(AUTHORIZATION))).thenReturn("Bearer token");

        ServiceFactory dbFactory = mock(ServiceFactory.class);
        JwtSecurity jwtSecurity = mock(JwtSecurity.class);
        when(jwtSecurity.validateToken(eq("token"))).thenReturn(empty());

        new JwtTokenFilter(dbFactory, jwtSecurity).filter(containerRequestContext);
        verifyUnauthorized(containerRequestContext);
    }

    @Test
    public void testUserNotFound() {
        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        when(containerRequestContext.getHeaderString(eq(AUTHORIZATION))).thenReturn("Bearer token");

        UserService userDB = mock(UserService.class);
        when(userDB.get(eq("userId"))).thenReturn(empty());
        ServiceFactory dbFactory = mock(ServiceFactory.class);
        when(dbFactory.getUserDB()).thenReturn(userDB);

        JwtSecurity jwtSecurity = mock(JwtSecurity.class);
        when(jwtSecurity.validateToken(eq("token"))).thenReturn(Optional.of("userId"));

        new JwtTokenFilter(dbFactory, jwtSecurity).filter(containerRequestContext);
        verifyUnauthorized(containerRequestContext);
    }

    @Test
    public void testValid() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("user").setDisplayName("User");

        UserService userDB = mock(UserService.class);
        when(userDB.get(eq("userId"))).thenReturn(Optional.of(user));
        ServiceFactory dbFactory = mock(ServiceFactory.class);
        when(dbFactory.getUserDB()).thenReturn(userDB);

        JwtSecurity jwtSecurity = mock(JwtSecurity.class);
        when(jwtSecurity.validateToken(eq("token"))).thenReturn(Optional.of("userId"));

        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        when(containerRequestContext.getHeaderString(eq(AUTHORIZATION))).thenReturn("Bearer token");

        new JwtTokenFilter(dbFactory, jwtSecurity).filter(containerRequestContext);

        verify(containerRequestContext, times(1)).setSecurityContext(argThat(securityContext ->
                securityContext.getUserPrincipal().equals(user)));
    }
}
