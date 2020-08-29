package vstocks.service.remote.youtube;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelStatistics;
import org.junit.Test;
import vstocks.service.remote.PriceCalculator;

import java.math.BigInteger;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class YouTubePriceCalculatorTest {
    private final Function<Integer, Channel> getChannelWithSubscribers = subscribers -> {
        ChannelStatistics channelStatistics = new ChannelStatistics()
                .setSubscriberCount(new BigInteger(String.valueOf(subscribers)));
        return new Channel().setStatistics(channelStatistics);
    };

    @Test
    public void testGetPrice() {
        PriceCalculator<Channel> priceCalculator = new YouTubePriceCalculator();
        /*
        Stream.iterate(0, i -> i == 0 ? 5 : (("" + i).contains("5") ? i * 2 : i * 5)).limit(17).forEach(i ->
                System.out.printf("Price: %d => %d\n", i, priceCalculator.getPrice(getChannelWithSubscribers.apply(i))));
         */

        assertEquals(10,    priceCalculator.getPrice(getChannelWithSubscribers.apply(0)));
        assertEquals(10,    priceCalculator.getPrice(getChannelWithSubscribers.apply(5)));
        assertEquals(10,    priceCalculator.getPrice(getChannelWithSubscribers.apply(10)));
        assertEquals(10,    priceCalculator.getPrice(getChannelWithSubscribers.apply(50)));
        assertEquals(10,    priceCalculator.getPrice(getChannelWithSubscribers.apply(100)));
        assertEquals(10,    priceCalculator.getPrice(getChannelWithSubscribers.apply(500)));
        assertEquals(10,    priceCalculator.getPrice(getChannelWithSubscribers.apply(1_000)));
        assertEquals(10,    priceCalculator.getPrice(getChannelWithSubscribers.apply(5_000)));
        assertEquals(11,    priceCalculator.getPrice(getChannelWithSubscribers.apply(10_000)));
        assertEquals(19,    priceCalculator.getPrice(getChannelWithSubscribers.apply(50_000)));
        assertEquals(29,    priceCalculator.getPrice(getChannelWithSubscribers.apply(100_000)));
        assertEquals(109,   priceCalculator.getPrice(getChannelWithSubscribers.apply(500_000)));
        assertEquals(209,   priceCalculator.getPrice(getChannelWithSubscribers.apply(1_000_000)));
        assertEquals(1009,  priceCalculator.getPrice(getChannelWithSubscribers.apply(5_000_000)));
        assertEquals(2009,  priceCalculator.getPrice(getChannelWithSubscribers.apply(10_000_000)));
        assertEquals(10005, priceCalculator.getPrice(getChannelWithSubscribers.apply(50_000_000)));
        assertEquals(20000, priceCalculator.getPrice(getChannelWithSubscribers.apply(100_000_000)));
    }
}
