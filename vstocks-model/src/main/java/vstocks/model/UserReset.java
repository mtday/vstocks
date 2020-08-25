package vstocks.model;

import java.util.Objects;

public class UserReset {
    private User user;
    private boolean reset;

    public UserReset() {
    }

    public User getUser() {
        return user;
    }

    public UserReset setUser(User user) {
        this.user = user;
        return this;
    }

    public boolean isReset() {
        return reset;
    }

    public UserReset setReset(boolean reset) {
        this.reset = reset;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserReset userReset = (UserReset) o;
        return reset == userReset.reset &&
                Objects.equals(user, userReset.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, reset);
    }

    @Override
    public String toString() {
        return "UserReset{" +
                "user=" + user +
                ", reset=" + reset +
                '}';
    }
}
