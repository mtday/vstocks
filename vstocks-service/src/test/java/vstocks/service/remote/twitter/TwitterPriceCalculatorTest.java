package vstocks.service.remote.twitter;

import org.junit.Test;
import twitter4j.User;
import vstocks.service.remote.PriceCalculator;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TwitterPriceCalculatorTest {
    private final Function<Integer, User> getUserWithFollowers = followers -> {
        User user = mock(User.class);
        when(user.getFollowersCount()).thenReturn(followers);
        return user;
    };

    @Test
    public void testGetPrice() {
        PriceCalculator<User> priceCalculator = new TwitterPriceCalculator();
        /*
        Stream.iterate(0, i -> i == 0 ? 5 : (("" + i).contains("5") ? i * 2 : i * 5)).limit(17).forEach(i ->
                System.out.printf("Price: %d => %d\n", i, priceCalculator.getPrice(getUserWithFollowers.apply(i))));
         */

        assertEquals(10,    priceCalculator.getPrice(getUserWithFollowers.apply(0)));
        assertEquals(10,    priceCalculator.getPrice(getUserWithFollowers.apply(5)));
        assertEquals(10,    priceCalculator.getPrice(getUserWithFollowers.apply(10)));
        assertEquals(10,    priceCalculator.getPrice(getUserWithFollowers.apply(50)));
        assertEquals(10,    priceCalculator.getPrice(getUserWithFollowers.apply(100)));
        assertEquals(10,    priceCalculator.getPrice(getUserWithFollowers.apply(500)));
        assertEquals(10,    priceCalculator.getPrice(getUserWithFollowers.apply(1_000)));
        assertEquals(10,    priceCalculator.getPrice(getUserWithFollowers.apply(5_000)));
        assertEquals(11,    priceCalculator.getPrice(getUserWithFollowers.apply(10_000)));
        assertEquals(19,    priceCalculator.getPrice(getUserWithFollowers.apply(50_000)));
        assertEquals(29,    priceCalculator.getPrice(getUserWithFollowers.apply(100_000)));
        assertEquals(109,   priceCalculator.getPrice(getUserWithFollowers.apply(500_000)));
        assertEquals(209,   priceCalculator.getPrice(getUserWithFollowers.apply(1_000_000)));
        assertEquals(1009,  priceCalculator.getPrice(getUserWithFollowers.apply(5_000_000)));
        assertEquals(2009,  priceCalculator.getPrice(getUserWithFollowers.apply(10_000_000)));
        assertEquals(10005, priceCalculator.getPrice(getUserWithFollowers.apply(50_000_000)));
        assertEquals(20000, priceCalculator.getPrice(getUserWithFollowers.apply(100_000_000)));
    }
}
