package vstocks.service.remote.twitter;

import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;

import java.util.ArrayList;

class CustomResponseList<T> extends ArrayList<T> implements ResponseList<T> {
    private static final long serialVersionUID = 1L;

    @Override
    public RateLimitStatus getRateLimitStatus() {
        return null;
    }

    @Override
    public int getAccessLevel() {
        return 0;
    }
}
