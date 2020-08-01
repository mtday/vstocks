package vstocks.service.remote;

import vstocks.model.Market;
import vstocks.service.remote.instagram.InstagramRemoteStockService;
import vstocks.service.remote.twitch.TwitchRemoteStockService;
import vstocks.service.remote.twitter.TwitterRemoteStockService;
import vstocks.service.remote.youtube.YouTubeRemoteStockService;

public class DefaultRemoteStockServiceFactory implements RemoteStockServiceFactory {
    private final RemoteStockService twitterRemoteStockService = new TwitterRemoteStockService();
    private final RemoteStockService youtubeRemoteStockService = new YouTubeRemoteStockService();
    private final RemoteStockService instagramRemoteStockService = new InstagramRemoteStockService();
    private final RemoteStockService twitchRemoteStockService = new TwitchRemoteStockService();

    @Override
    public RemoteStockService getForMarket(Market market) {
        switch (market) {
            case TWITTER:
                return twitterRemoteStockService;
            case YOUTUBE:
                return youtubeRemoteStockService;
            case INSTAGRAM:
                return instagramRemoteStockService;
            case TWITCH:
                return twitchRemoteStockService;
        }
        throw new RuntimeException("Unsupported market: " + market);
    }
}
