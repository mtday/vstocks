package vstocks.rest.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.model.User;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Provider
public class AccessLogFilter implements ContainerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Optional<User> user = ofNullable(requestContext.getProperty("user"))
                .filter(u -> u instanceof User)
                .map(u -> (User) u);

        String username = String.format("%-16s", user.map(User::getUsername).orElse(""));
        String method = requestContext.getRequest().getMethod();
        String path = requestContext.getUriInfo().getRequestUri().getPath();
        LOGGER.info("{} => {} {}", username, method, path);
    }
}
