package vstocks.model;

import com.google.common.collect.Range;

import java.time.Instant;
import java.util.List;

public class ActivityLogSearch {
    private List<String> ids;
    private List<String> userIds;
    private List<ActivityType> types;
    private Range<Instant> timestampRange;
    private List<Market> markets;
    private List<String> symbols;
    private Range<Integer> sharesRange;
    private Range<Integer> priceRange;

    public ActivityLogSearch() {
    }

    public List<String> getIds() {
        return ids;
    }

    public ActivityLogSearch setIds(List<String> ids) {
        this.ids = ids;
        return this;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public ActivityLogSearch setUserIds(List<String> userIds) {
        this.userIds = userIds;
        return this;
    }

    public List<ActivityType> getTypes() {
        return types;
    }

    public ActivityLogSearch setTypes(List<ActivityType> types) {
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

    public List<Market> getMarkets() {
        return markets;
    }

    public ActivityLogSearch setMarkets(List<Market> markets) {
        this.markets = markets;
        return this;
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public ActivityLogSearch setSymbols(List<String> symbols) {
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
