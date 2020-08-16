package vstocks.model;

import static java.util.Locale.ENGLISH;

public enum DatabaseField {
    ACHIEVEMENT_ID,
    CREDITS,
    DESCRIPTION,
    DIFFICULTY,
    DISPLAY_NAME,
    EMAIL,
    ID,
    MARKET,
    NAME,
    PRICE,
    RANK,
    SHARES,
    SYMBOL,
    TIMESTAMP,
    TOTAL,
    TYPE,
    USERNAME,
    USER_ID;

    public Sort toSort() {
        return new Sort(this);
    }

    public Sort toSort(SortDirection direction) {
        return new Sort(this, direction);
    }

    public String getField() {
        return name().toLowerCase(ENGLISH);
    }
}
