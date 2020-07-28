package vstocks.model;

public enum UserSource {
    TWITTER,
    FACEBOOK,
    GOOGLE;

    public static UserSource fromClientName(String clientName) {
        switch (clientName) {
            case "TwitterClient":
                return TWITTER;
            case "FacebookClient":
                return FACEBOOK;
            case "GoogleClient":
                return GOOGLE;
        }
        throw new RuntimeException("Unrecognized client: " + clientName);
    }
}
