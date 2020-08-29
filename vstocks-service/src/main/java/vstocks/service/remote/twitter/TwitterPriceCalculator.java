package vstocks.service.remote.twitter;

import twitter4j.User;
import vstocks.service.remote.PriceCalculator;

public class TwitterPriceCalculator implements PriceCalculator<User> {
    public long getPrice(User user) {
        double followers = user.getFollowersCount();

        /*
        // Scale the number of followers into a value between 0.0 and 1.0 using a sigmoid function. This value
        // is called the "scaledPrice" below. The sigmoid function is used to give it a slight "S" shape so that
        // the price scales up slowly and flattens out at the top. Since the sigmoid range used [-6, 4] doesn't
        // hit 0 at the -6 minimum value, we subtract 0.00247 to bring that minimum value really close to 0.
        // This value is then used to find the actual price as a value in the range [1, 100_000]
        // (the valid price range).

        // Visualize the sigmoid scaled price via wolframalpha.com using:
        // plot (1 / (1 + e^(-( (-6 + ((4 - -6) / 100000000) * x) )))) - 0.00247, {x, 0, 100000000}
        double sigmoidScaleMin = -6, sigmoidScaleMax = 4;
        double maxFollowers = 100_000_000; // an approximation
        double scaledFollowers = sigmoidScaleMin + ((sigmoidScaleMax - sigmoidScaleMin) / maxFollowers) * followers;
        double scaledPrice = (1 / (1 + Math.pow(Math.E, -scaledFollowers))) - 0.00247;

        // Visualize the price function via wolframalpha.com using:
        // plot  1 + (100000 - 1) * ((1 / (1 + e^(-( (-6 + ((4 - -6) / 100000000) * x) )))) - 0.00247), {x, 0, 100000000}
        double validPriceMin = 10, validPriceMax = 100_000;
        return (long) (validPriceMin + ((validPriceMax - validPriceMin) * scaledPrice));
         */

        // Now using linear pricing
        // Visualize the price function via wolframalpha.com using:
        // plot  10 + (20000 - 10) * (1 / 100000000 * x), {x, 0, 100000000}
        double maxFollowers = 100_000_000; // an approximation
        double scaledPrice = (1.0 / maxFollowers) * followers;

        double validPriceMin = 10, validPriceMax = 20_000;
        return (long) (validPriceMin + ((validPriceMax - validPriceMin) * scaledPrice));
    }
}
