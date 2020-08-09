package vstocks.model;

import java.util.Objects;

public class UserCredits {
    private String userId;
    private long credits;

    public UserCredits() {
    }

    public String getUserId() {
        return userId;
    }

    public UserCredits setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public long getCredits() {
        return credits;
    }

    public UserCredits setCredits(long credits) {
        this.credits = credits;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCredits that = (UserCredits) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "UserCredits{" +
                "userId='" + userId + '\'' +
                ", credits=" + credits +
                '}';
    }
}
