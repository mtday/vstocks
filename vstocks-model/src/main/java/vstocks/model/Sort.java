package vstocks.model;

import java.util.Objects;

public class Sort {
    public enum SortDirection {
        ASC,
        DESC
    }

    private DatabaseField field;
    private SortDirection direction = SortDirection.ASC;

    public Sort() {
    }

    public Sort(DatabaseField field) {
        this(field, SortDirection.ASC);
    }

    public Sort(DatabaseField field, SortDirection direction) {
        this.field = field;
        this.direction = direction;
    }

    public static Sort parse(String sort) {
        if (sort.contains(":")) {
            String[] parts = sort.split(":", 2);
            return new Sort(DatabaseField.valueOf(parts[0]), SortDirection.valueOf(parts[1]));
        }
        return new Sort(DatabaseField.valueOf(sort));
    }

    public DatabaseField getField() {
        return field;
    }

    public Sort setField(DatabaseField field) {
        this.field = field;
        return this;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public Sort setDirection(SortDirection direction) {
        this.direction = direction;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sort sort = (Sort) o;
        return field == sort.field &&
                direction == sort.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field.name(), direction.name());
    }

    @Override
    public String toString() {
        return String.format("%s %s", field.getField(), direction.name());
    }
}
