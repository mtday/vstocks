package vstocks.model;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Market {
    private String id;
    private String name;

    public Market() {
    }

    public String getId() {
        return id;
    }

    public Market setId(String id) {
        this.id = requireNonNull(id);
        return this;
    }

    public String getName() {
        return name;
    }

    public Market setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Market market = (Market) o;
        return Objects.equals(id, market.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Market{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
