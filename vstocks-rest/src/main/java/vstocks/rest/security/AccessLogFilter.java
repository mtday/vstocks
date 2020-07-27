package vstocks.rest.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.security.Principal;

import static java.util.Optional.ofNullable;

@Provider
public class AccessLogFilter implements ContainerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String username = String.format("%-16s",
                ofNullable(requestContext.getSecurityContext().getUserPrincipal())
                        .map(Principal::getName)
                        .orElse("")
        );
        String method = requestContext.getRequest().getMethod();
        String path = requestContext.getUriInfo().getRequestUri().getPath();
        LOGGER.info("{} => {} {}", username, method, path);
    }
}
