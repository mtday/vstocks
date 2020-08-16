package vstocks.model.rest;

import java.util.Objects;

public class UsernameExistsResponse {
    private String username;
    private boolean exists;

    public UsernameExistsResponse() {
    }

    public String getUsername() {
        return username;
    }

    public UsernameExistsResponse setUsername(String username) {
        this.username = username;
        return this;
    }

    public boolean isExists() {
        return exists;
    }

    public UsernameExistsResponse setExists(boolean exists) {
        this.exists = exists;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsernameExistsResponse that = (UsernameExistsResponse) o;
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
