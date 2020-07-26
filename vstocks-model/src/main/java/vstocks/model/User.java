package vstocks.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class User {
    private String id;
    private String username;
    private String email;
    private UserSource source;
    private String hashedPass;

    public User() {
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = requireNonNull(id);
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = requireNonNull(username);
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = requireNonNull(email);
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

    @JsonIgnore
    public String getHashedPass() {
        return hashedPass;
    }

    public User setHashedPass(String hashedPass) {
        this.hashedPass = requireNonNull(hashedPass);
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
                ", email='" + email + '\'' +
                ", source=" + source +
                ", hashedPass='" + hashedPass + '\'' +
                '}';
    }
}
