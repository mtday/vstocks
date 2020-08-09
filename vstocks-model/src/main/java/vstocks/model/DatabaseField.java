package vstocks.model;

import static java.util.Locale.ENGLISH;

public enum DatabaseField {
    ACHIEVEMENT_ID,
    BALANCE,
    DESCRIPTION,
    DIFFICULTY,
    DISPLAY_NAME,
    EMAIL,
    ID,
    MARKET,
    NAME,
    PRICE,
    SHARES,
    SYMBOL,
    TIMESTAMP,
    TYPE,
    USERNAME,
    USER_ID;

    public Sort toSort() {
        return new Sort(this);
    }

    public Sort toSort(Sort.SortDirection direction) {
        return new Sort(this, direction);
    }

    public String getField() {
        return name().toLowerCase(ENGLISH);
    }
}