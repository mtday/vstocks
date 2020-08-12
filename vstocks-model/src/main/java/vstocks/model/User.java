package vstocks.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.security.Principal;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ENGLISH;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public class User implements Principal {
    private String id;
    private String email;
    private String username;
    private String displayName;

    public User() {
    }

    @JsonIgnore
    public Map<String, Object> getJwtClaims() {
        Map<String, Object> claims = new TreeMap<>();
        claims.put("id", id);
        claims.put("email", email);
        claims.put("username", username);
        claims.put("displayName", displayName);
        return claims;
    }

    public static Optional<User> getUserFromJwtClaims(Map<String, Object> claims) {
        return Stream.of(claims)
                .filter(Objects::nonNull)
                .filter(claimsMap -> claimsMap.containsKey("id"))
                .filter(claimsMap -> claimsMap.containsKey("email"))
                .filter(claimsMap -> claimsMap.containsKey("username"))
                .filter(claimsMap -> claimsMap.containsKey("displayName"))
                .filter(claimsMap -> {
                    // Make sure the user id matches the id generated from the email.
                    String id = String.valueOf(claimsMap.get("id"));
                    String email = String.valueOf(claimsMap.get("email"));
                    return User.generateId(email).equals(id);
                })
                .map(claimsMap -> new User()
                        .setEmail(String.valueOf(claimsMap.get("email"))) // also sets the id
                        .setUsername(String.valueOf(claimsMap.get("username")))
                        .setDisplayName(String.valueOf(claimsMap.get("displayName"))))
                .findFirst();
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
                '}';
    }
}
