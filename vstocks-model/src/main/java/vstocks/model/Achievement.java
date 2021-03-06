package vstocks.model;

import java.util.Objects;

public class Achievement {
    private String id;
    private String name;
    private AchievementCategory category;
    private String description;
    private int order = 0;

    public Achievement() {
    }

    public String getId() {
        return id;
    }

    public Achievement setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Achievement setName(String name) {
        this.name = name;
        return this;
    }

    public AchievementCategory getCategory() {
        return category;
    }

    public Achievement setCategory(AchievementCategory category) {
        this.category = category;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Achievement setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getOrder() {
        return order;
    }

    public Achievement setOrder(int order) {
        this.order = order;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Achievement that = (Achievement) o;
        return order == that.order &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                category == that.category &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category.name(), description, order);
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", description='" + description + '\'' +
                ", order=" + order +
                '}';
    }
}
