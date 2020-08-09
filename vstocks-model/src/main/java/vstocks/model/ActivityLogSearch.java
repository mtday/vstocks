package vstocks.model;

import com.google.common.collect.Range;

import java.time.Instant;
import java.util.Set;

public class ActivityLogSearch {
    private Set<String> ids;
    private Set<String> userIds;
    private Set<ActivityType> types;
    private Range<Instant> timestampRange;
    private Set<Market> markets;
    private Set<String> symbols;
    private Range<Integer> sharesRange;
    private Range<Integer> priceRange;

    public ActivityLogSearch() {
    }

    public Set<String> getIds() {
        return ids;
    }

    public ActivityLogSearch setIds(Set<String> ids) {
        this.ids = ids;
        return this;
    }

    public Set<String> getUserIds() {
        return userIds;
    }

    public ActivityLogSearch setUserIds(Set<String> userIds) {
        this.userIds = userIds;
        return this;
    }

    public Set<ActivityType> getTypes() {
        return types;
    }

    public ActivityLogSearch setTypes(Set<ActivityType> types) {
        this.types = types;
        return this;
    }

    public Range<Instant> getTimestampRange() {
        return timestampRange;
    }

    public ActivityLogSearch setTimestampRange(Range<Instant> timestampRange) {
        this.timestampRange = timestampRange;
        return this;
    }

    public Set<Market> getMarkets() {
        return markets;
    }

    public ActivityLogSearch setMarkets(Set<Market> markets) {
        this.markets = markets;
        return this;
    }

    public Set<String> getSymbols() {
        return symbols;
    }

    public ActivityLogSearch setSymbols(Set<String> symbols) {
        this.symbols = symbols;
        return this;
    }

    public Range<Integer> getSharesRange() {
        return sharesRange;
    }

    public ActivityLogSearch setSharesRange(Range<Integer> sharesRange) {
        this.sharesRange = sharesRange;
        return this;
    }

    public Range<Integer> getPriceRange() {
        return priceRange;
    }

    public ActivityLogSearch setPriceRange(Range<Integer> priceRange) {
        this.priceRange = priceRange;
        return this;
    }

    @Override
    public String toString() {
        return "ActivityLogSearch{" +
                "ids=" + ids +
                ", userIds=" + userIds +
                ", types=" + types +
                ", timestampRange=" + timestampRange +
                ", markets=" + markets +
                ", symbols=" + symbols +
                ", sharesRange=" + sharesRange +
                ", priceRange=" + priceRange +
                '}';
    }
}
