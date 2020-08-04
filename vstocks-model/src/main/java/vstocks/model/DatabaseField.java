package vstocks.model;

import static java.util.Locale.ENGLISH;

public enum DatabaseField {
    BALANCE,
    DISPLAY_NAME,
    ID,
    MARKET,
    NAME,
    PRICE,
    SHARES,
    SOURCE,
    SYMBOL,
    TIMESTAMP,
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
