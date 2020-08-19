package vstocks.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserCountCollection {
    private List<UserCount> userCounts;
    private Map<DeltaInterval, Delta> deltas;

    public UserCountCollection() {
    }

    public List<UserCount> getUserCounts() {
        return userCounts;
    }

    public UserCountCollection setUserCounts(List<UserCount> userCounts) {
        this.userCounts = userCounts;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public UserCountCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCountCollection that = (UserCountCollection) o;
        return Objects.equals(userCounts, that.userCounts) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userCounts, deltas);
    }

    @Override
    public String toString() {
        return "UserCountCollection{" +
                "userCounts=" + userCounts +
                ", deltas=" + deltas +
                '}';
    }
}
