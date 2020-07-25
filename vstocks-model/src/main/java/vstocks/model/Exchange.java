package vstocks.model;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class Exchange {
    private String id;
    private String name;

    public Exchange() {
    }

    private Exchange(Exchange exchange) {
        this.id = exchange.id;
        this.name = exchange.name;
    }

    public String getId() {
        return id;
    }

    public Exchange setId(String id) {
        this.id = requireNonNull(id);
        return this;
    }

    public String getName() {
        return name;
    }

    public Exchange setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exchange exchange = (Exchange) o;
        return Objects.equals(id, exchange.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Exchange{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
