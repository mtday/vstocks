package vstocks.model;

import java.util.Objects;

public class UsernameCheck {
    private String username;
    private boolean exists;
    private boolean valid;
    private String message;

    public UsernameCheck() {
    }

    public String getUsername() {
        return username;
    }

    public UsernameCheck setUsername(String username) {
        this.username = username;
        return this;
    }

    public boolean isExists() {
        return exists;
    }

    public UsernameCheck setExists(boolean exists) {
        this.exists = exists;
        return this;
    }

    public boolean isValid() {
        return valid;
    }

    public UsernameCheck setValid(boolean valid) {
        this.valid = valid;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public UsernameCheck setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsernameCheck that = (UsernameCheck) o;
        return exists == that.exists &&
                valid == that.valid &&
                Objects.equals(username, that.username) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, exists, valid, message);
    }

    @Override
    public String toString() {
        return "UsernameCheck{" +
                "username='" + username + '\'' +
                ", exists=" + exists +
                ", valid=" + valid +
                ", message='" + message + '\'' +
                '}';
    }
}
