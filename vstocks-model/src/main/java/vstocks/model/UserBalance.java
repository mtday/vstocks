package vstocks.model;

import java.util.Objects;

public class UserBalance {
    private String userId;
    private int balance;

    public UserBalance() {
    }

    public String getUserId() {
        return userId;
    }

    public UserBalance setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public int getBalance() {
        return balance;
    }

    public UserBalance setBalance(int balance) {
        this.balance = balance;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserBalance that = (UserBalance) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "UserBalance{" +
                "userId='" + userId + '\'' +
                ", balance=" + balance +
                '}';
    }
}
