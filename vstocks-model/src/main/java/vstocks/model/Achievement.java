package vstocks.model;

import java.util.Comparator;
import java.util.Objects;

public class Achievement {
    private String id;
    private String name;
    private AchievementCategory category;
    private String description;
    private int order = 0;

    public Achievement() {
    }

    public static final Comparator<Achievement> FULL_COMPARATOR = Comparator
            .comparing(Achievement::getId)
            .thenComparing(Achievement::getName)
            .thenComparing(Achievement::getCategory)
            .thenComparing(Achievement::getDescription)
            .thenComparingInt(Achievement::getOrder);

    public static final Comparator<Achievement> UNIQUE_COMPARATOR = Comparator.comparing(Achievement::getId);

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
        Achievement achievement = (Achievement) o;
        return Objects.equals(id, achievement.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
