package vstocks.rest.security;

import org.pac4j.core.config.Config;
import org.pac4j.jax.rs.pac4j.JaxRsUrlResolver;
import org.pac4j.jax.rs.servlet.pac4j.ServletSessionStore;
import org.pac4j.oauth.client.TwitterClient;

import static vstocks.config.Config.*;

public class SecurityConfig {
    public static Config getConfig() {
        TwitterClient twitterClient = new TwitterClient(TWITTER_API_KEY.getString(), TWITTER_API_SECRET.getString());
        twitterClient.setCallbackUrl(TWITTER_API_CALLBACK.getString());

        Config config = new Config(twitterClient);
        config.getClients().setUrlResolver(new JaxRsUrlResolver());
        config.setSessionStore(new ServletSessionStore());
        return config;
    }
}
