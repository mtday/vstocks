package vstocks.rest.security;

import org.junit.Test;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccessLogFilterTest {
    private ContainerRequestContext getRequestContext(String method, String path) {
        Request request = mock(Request.class);
        when(request.getMethod()).thenReturn(method);

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getRequestUri()).thenReturn(URI.create(path));

        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        when(containerRequestContext.getRequest()).thenReturn(request);
        when(containerRequestContext.getUriInfo()).thenReturn(uriInfo);
        return containerRequestContext;
    }

    private String test(ContainerRequestContext containerRequestContext) {
        List<String> logs = new ArrayList<>();
        new AccessLogFilter(logs::add).filter(containerRequestContext);
        assertEquals(1, logs.size());
        return logs.iterator().next();
    }

    @Test
    public void testNoSecurityContext() {
        ContainerRequestContext containerRequestContext = getRequestContext("GET", "/api/v1/user");
        assertEquals("- => GET /api/v1/user", test(containerRequestContext));
    }

    @Test
    public void testNoSecurityContextJwt() {
        ContainerRequestContext containerRequestContext = getRequestContext("GET", "/api/v1/user");
        when(containerRequestContext.getHeaderString(eq(AUTHORIZATION))).thenReturn("Bearer token");
        assertEquals("- => GET /api/v1/user (jwt)", test(containerRequestContext));
    }

    @Test
    public void testNoUserPrincipal() {
        SecurityContext securityContext = mock(SecurityContext.class);
        ContainerRequestContext containerRequestContext = getRequestContext("GET", "/api/v1/user");
        when(containerRequestContext.getSecurityContext()).thenReturn(securityContext);
        assertEquals("- => GET /api/v1/user", test(containerRequestContext));
    }

    @Test
    public void testWithPrincipal() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("username");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getUserPrincipal()).thenReturn(principal);

        ContainerRequestContext containerRequestContext = getRequestContext("POST", "/api/v1/markets/TWITTER/stock/vstocks/buy");
        when(containerRequestContext.getSecurityContext()).thenReturn(securityContext);
        assertEquals("username => POST /api/v1/markets/TWITTER/stock/vstocks/buy", test(containerRequestContext));
    }

    @Test
    public void testWithPrincipalAndToken() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("username");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getUserPrincipal()).thenReturn(principal);

        ContainerRequestContext containerRequestContext = getRequestContext("PUT", "/api/v1/markets/TWITTER/stock/vstocks");
        when(containerRequestContext.getSecurityContext()).thenReturn(securityContext);
        when(containerRequestContext.getHeaderString(eq(AUTHORIZATION))).thenReturn("Bearer token");
        assertEquals("username => PUT /api/v1/markets/TWITTER/stock/vstocks (jwt)", test(containerRequestContext));
    }
}
