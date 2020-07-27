package vstocks.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.security.Principal;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class User implements Principal {
    private String id;
    private String username;
    private UserSource source;
    private String displayName;

    public User() {
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = requireNonNull(id);
        return this;
    }

    @JsonIgnore
    @Override
    public String getName() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = requireNonNull(username);
        return this;
    }

    @JsonIgnore
    public UserSource getSource() {
        return source;
    }

    public User setSource(UserSource source) {
        this.source = requireNonNull(source);
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public User setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", source=" + source +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
