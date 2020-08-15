package vstocks.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.security.Principal;
import java.util.Objects;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ENGLISH;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public class User implements Principal {
    private String id;
    private String email;
    private String username;
    private String displayName;
    private String imageLink;

    public User() {
    }

    public static String generateId(String email) {
        return ofNullable(email)
                .map(e -> UUID.nameUUIDFromBytes(e.trim().toLowerCase(ENGLISH).getBytes(UTF_8)).toString())
                .orElse(null);
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = requireNonNull(id);
        return this;
    }

    @JsonIgnore
    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = requireNonNull(email);
        return setId(generateId(email));
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

    public String getDisplayName() {
        return displayName;
    }

    public User setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getImageLink() {
        return imageLink;
    }

    public User setImageLink(String imageLink) {
        this.imageLink = imageLink;
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
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", imageLink='" + imageLink + '\'' +
                '}';
    }
}
