package vstocks.model;

import java.util.Objects;

public class UsernameExists {
    private String username;
    private boolean exists;

    public UsernameExists() {
    }

    public String getUsername() {
        return username;
    }

    public UsernameExists setUsername(String username) {
        this.username = username;
        return this;
    }

    public boolean isExists() {
        return exists;
    }

    public UsernameExists setExists(boolean exists) {
        this.exists = exists;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsernameExists that = (UsernameExists) o;
        return exists == that.exists &&
                Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, exists);
    }

    @Override
    public String toString() {
        return "UsernameExistsResponse{" +
                "username='" + username + '\'' +
                ", exists=" + exists +
                '}';
    }
}
