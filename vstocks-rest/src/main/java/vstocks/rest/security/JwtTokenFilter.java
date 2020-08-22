package vstocks.rest.security;

import vstocks.db.ServiceFactory;
import vstocks.model.ErrorResponse;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static javax.ws.rs.Priorities.AUTHENTICATION;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Provider
@JwtTokenRequired
@Priority(AUTHENTICATION)
public class JwtTokenFilter implements ContainerRequestFilter {
    public static final String INVALID_JWT_MESSAGE = "Missing or invalid JWT authorization bearer token";

    private final ServiceFactory dbFactory;
    private final JwtSecurity jwtSecurity;

    @Inject
    public JwtTokenFilter(ServiceFactory dbFactory, JwtSecurity jwtSecurity) {
        this.dbFactory = dbFactory;
        this.jwtSecurity = jwtSecurity;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Optional<SecurityContext> securityContext = Stream.of(requestContext.getHeaderString(AUTHORIZATION))
                .filter(Objects::nonNull)
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .map(authHeader -> authHeader.substring("Bearer ".length()))
                .map(jwtSecurity::validateToken)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .flatMap(userId -> dbFactory.getUserDB().get(userId))
                .map(UserSecurityContext::new);

        if (securityContext.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse()
                    .setStatus(UNAUTHORIZED.getStatusCode())
                    .setMessage(INVALID_JWT_MESSAGE);
            Response response = Response
                    .status(UNAUTHORIZED)
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .entity(errorResponse)
                    .build();
            requestContext.abortWith(response);
        } else {
            requestContext.setSecurityContext(securityContext.get());
        }
    }
}
