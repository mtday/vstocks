package vstocks.model;

import com.google.common.collect.Range;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static vstocks.model.ActivityType.STOCK_BUY;
import static vstocks.model.ActivityType.STOCK_SELL;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;

public class ActivityLogSearchTest {
    @Test
    public void testGettersAndSettersAll() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLogSearch activityLogSearch = new ActivityLogSearch()
                .setIds(new LinkedHashSet<>(asList("id1", "id2")))
                .setUserIds(new LinkedHashSet<>(asList("user1", "user2")))
                .setTypes(new LinkedHashSet<>(asList(STOCK_SELL, STOCK_BUY)))
                .setTimestampRange(Range.closed(now.minusSeconds(5), now.plusSeconds(5)))
                .setMarkets(new LinkedHashSet<>(asList(TWITTER, YOUTUBE)))
                .setSymbols(new LinkedHashSet<>(asList("symbol1", "symbol2")))
                .setSharesRange(Range.atLeast(10))
                .setPriceRange(Range.atMost(20));

        assertEquals(new LinkedHashSet<>(asList("id1", "id2")), activityLogSearch.getIds());
        assertEquals(new LinkedHashSet<>(asList("user1", "user2")), activityLogSearch.getUserIds());
        assertEquals(new LinkedHashSet<>(asList(STOCK_SELL, STOCK_BUY)), activityLogSearch.getTypes());
        assertEquals(Range.closed(now.minusSeconds(5), now.plusSeconds(5)), activityLogSearch.getTimestampRange());
        assertEquals(new LinkedHashSet<>(asList(TWITTER, YOUTUBE)), activityLogSearch.getMarkets());
        assertEquals(new LinkedHashSet<>(asList("symbol1", "symbol2")), activityLogSearch.getSymbols());
        assertEquals(Range.atLeast(10), activityLogSearch.getSharesRange());
        assertEquals(Range.atMost(20), activityLogSearch.getPriceRange());
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant tsBegin = now.minusSeconds(5);
        Instant tsEnd = now.plusSeconds(5);
        ActivityLogSearch activityLogSearch = new ActivityLogSearch()
                .setIds(new LinkedHashSet<>(asList("id1", "id2")))
                .setUserIds(new LinkedHashSet<>(asList("user1", "user2")))
                .setTypes(new LinkedHashSet<>(asList(STOCK_SELL, STOCK_BUY)))
                .setTimestampRange(Range.closed(tsBegin, tsEnd))
                .setMarkets(new LinkedHashSet<>(asList(TWITTER, YOUTUBE)))
                .setSymbols(new LinkedHashSet<>(asList("symbol1", "symbol2")))
                .setSharesRange(Range.atLeast(10))
                .setPriceRange(Range.atMost(20));
        assertEquals("ActivityLogSearch{ids=[id1, id2], userIds=[user1, user2], types=[STOCK_SELL, STOCK_BUY], "
                + "timestampRange=[" + tsBegin + ".." + tsEnd + "], markets=[TWITTER, YOUTUBE], "
                + "symbols=[symbol1, symbol2], sharesRange=[10..+∞), priceRange=(-∞..20]}", activityLogSearch.toString());
    }
}
