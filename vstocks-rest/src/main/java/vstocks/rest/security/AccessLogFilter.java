package vstocks.rest.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.security.Principal;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Provider
public class AccessLogFilter implements ContainerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger("access-log");

    private final Consumer<String> logConsumer;

    public AccessLogFilter() {
        this(LOGGER::info);
    }

    public AccessLogFilter(Consumer<String> logConsumer) {
        this.logConsumer = logConsumer;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String username = ofNullable(requestContext.getSecurityContext())
                .map(SecurityContext::getUserPrincipal)
                .map(Principal::getName)
                .orElse("-");
        String method = requestContext.getRequest().getMethod();
        String path = requestContext.getUriInfo().getRequestUri().getPath();
        boolean hasToken = Stream.of(requestContext.getHeaderString(AUTHORIZATION))
                .filter(Objects::nonNull)
                .anyMatch(header -> header.startsWith("Bearer "));
        logConsumer.accept(format("%s => %s %s%s", username, method, path, hasToken ? " (jwt)" : ""));
    }
}
