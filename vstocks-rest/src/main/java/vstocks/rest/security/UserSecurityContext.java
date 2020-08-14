package vstocks.rest.security;

import vstocks.model.User;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

import static java.util.Objects.requireNonNull;

public class UserSecurityContext implements SecurityContext {
    private static final String AUTHENTICATION_SCHEME = "user";
    private final User user;

    public UserSecurityContext(User user) {
        this.user = requireNonNull(user);
    }

    @Override
    public Principal getUserPrincipal() {
        return user;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public String getAuthenticationScheme() {
        return AUTHENTICATION_SCHEME;
    }
}
