package vstocks.service.remote.youtube;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static vstocks.config.Config.GOOGLE_API_CREDENTIALS;
import static vstocks.config.Config.GOOGLE_API_SCOPES;

public class YouTubeService {
    private final YouTube youTube;

    public YouTubeService() {
        List<String> scopes = Stream.of(GOOGLE_API_SCOPES.getString())
                .map(delimitedScopes -> delimitedScopes.split(";"))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .filter(scope -> !scope.isEmpty())
                .collect(toList());

        try (InputStream inputStream = GOOGLE_API_CREDENTIALS.getInputStream()) {
            ServiceAccountCredentials credentials =
                    (ServiceAccountCredentials) GoogleCredentials.fromStream(inputStream).createScoped(scopes);
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

            NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            youTube = new YouTube.Builder(transport, jsonFactory, requestInitializer)
                    .setApplicationName(credentials.getProjectId())
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Channel> getChannel(String channelId) {
        try {
            ChannelListResponse response = youTube.channels().list("snippet,statistics")
                    .setId(channelId)
                    .setMaxResults((long) 1)
                    .execute();
            return Stream.of(response)
                    .map(ChannelListResponse::getItems)
                    .filter(Objects::nonNull) // items are null when there are no results
                    .flatMap(Collection::stream)
                    .findFirst();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Channel> getChannels(Set<String> channelIds) {
        if (channelIds.isEmpty()) {
            return emptyList();
        }

        // The YouTube api doesn't seem to support batch lookups
        return channelIds.stream()
                .map(this::getChannel)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    public List<Channel> search(String search, int limit) {
        try {
            SearchListResponse response = youTube.search().list("snippet")
                    .setQ(search)
                    .setMaxResults((long) limit)
                    .setType("channel")
                    .execute();
            Set<String> channelIds = Stream.of(response)
                    .map(SearchListResponse::getItems)
                    .filter(Objects::nonNull) // items null when no match found
                    .flatMap(Collection::stream)
                    .map(searchResult -> searchResult.getId().getChannelId())
                    .collect(toSet());
            return getChannels(channelIds);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
