package vstocks.model;

import java.util.Arrays;
import java.util.Optional;

public enum UserSource {
    TwitterClient("TW"),
    FacebookClient("FB"),
    GoogleClient("GO");

    private final String abbreviation;

    UserSource(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static Optional<UserSource> fromClientName(String clientName) {
        return Arrays.stream(values())
                .filter(market -> market.getAbbreviation().equalsIgnoreCase(clientName) || market.name().equalsIgnoreCase(clientName))
                .findFirst();
    }
}
