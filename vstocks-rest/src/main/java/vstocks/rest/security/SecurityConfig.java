package vstocks.rest.security;

import org.pac4j.core.config.Config;
import org.pac4j.jax.rs.pac4j.JaxRsUrlResolver;
import org.pac4j.jax.rs.servlet.pac4j.ServletSessionStore;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.oauth.client.TwitterClient;

import static vstocks.config.Config.*;

public class SecurityConfig {
    public static Config getConfig() {
        TwitterClient twitterClient = new TwitterClient(
                TWITTER_API_CONSUMER_KEY.getString(),
                TWITTER_API_CONSUMER_SECRET.getString()
        );
        twitterClient.setCallbackUrl(TWITTER_API_CALLBACK.getString());
        twitterClient.setIncludeEmail(true);

        Google2Client google2Client = new Google2Client(
                GOOGLE_API_CLIENT_ID.getString(),
                GOOGLE_API_CLIENT_SECRET.getString()
        );
        google2Client.setCallbackUrl(GOOGLE_API_CALLBACK.getString());

        FacebookClient facebookClient = new FacebookClient(
                FACEBOOK_API_CLIENT_ID.getString(),
                FACEBOOK_API_CLIENT_SECRET.getString()
        );
        facebookClient.setCallbackUrl(FACEBOOK_API_CALLBACK.getString());
        facebookClient.setScope("email");

        Config config = new Config(twitterClient, google2Client, facebookClient);
        config.getClients().setUrlResolver(new JaxRsUrlResolver());
        config.setSessionStore(new ServletSessionStore());
        return config;
    }
}
