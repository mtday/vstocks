package vstocks.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.pac4j.core.profile.CommonProfile;
import vstocks.achievement.AchievementService;
import vstocks.db.ServiceFactory;
import vstocks.model.*;
import vstocks.model.portfolio.MarketRankCollection;
import vstocks.model.portfolio.RankedUser;
import vstocks.rest.security.JwtSecurity;
import vstocks.service.remote.RemoteStockServiceFactory;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.logging.LogManager;

import static org.mockito.Mockito.mock;
import static vstocks.model.User.generateId;

public abstract class ResourceTest extends JerseyTest {
    static {
        // Disable the java.util.logging used by the embedded webserver used in the tests
        LogManager.getLogManager().reset();
    }

    private ObjectMapper objectMapper;
    private ServiceFactory serviceFactory;
    private RemoteStockServiceFactory remoteStockServiceFactory;
    private AchievementService achievementService;
    private JwtSecurity jwtSecurity;

    @Override
    protected ResourceConfig configure() {
        objectMapper = Application.getObjectMapper();
        serviceFactory = mock(ServiceFactory.class);
        remoteStockServiceFactory = mock(RemoteStockServiceFactory.class);
        achievementService = mock(AchievementService.class);
        jwtSecurity = mock(JwtSecurity.class);

        Environment environment = new Environment()
                .setServiceFactory(serviceFactory)
                .setRemoteStockServiceFactory(remoteStockServiceFactory)
                .setAchievementService(achievementService)
                .setJwtSecurity(jwtSecurity)
                .setIncludeSecurity(false) // disables Pac4j, we include a simple profile below
                .setIncludeBackgroundTasks(false);

        Application application = new Application(environment);
        application.register(new CommonProfileValueParamProvider(getCommonProfile()));
        return application;
    }

    public User getUser() {
        return new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("username")
                .setDisplayName("Display Name")
                .setProfileImage("https://domain.com/user/profile-image.png");
    }

    public CommonProfile getCommonProfile() {
        User user = getUser();
        CommonProfile commonProfile = new CommonProfile();
        commonProfile.setClientName("TwitterClient");
        commonProfile.setId("12345");
        commonProfile.addAttribute("email", user.getEmail());
        commonProfile.addAttribute("username", user.getUsername());
        commonProfile.addAttribute("display_name", user.getDisplayName());
        commonProfile.addAttribute("profile_url", URI.create(user.getProfileImage()));
        return commonProfile;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    public RemoteStockServiceFactory getRemoteStockServiceFactory() {
        return remoteStockServiceFactory;
    }

    public AchievementService getAchievementService() {
        return achievementService;
    }

    public JwtSecurity getJwtSecurity() {
        return jwtSecurity;
    }

    public <T> T convert(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T convert(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class AchievementListTypeRef extends TypeReference<List<Achievement>> {};
    public static class MarketListTypeRef extends TypeReference<List<Market>> {}
    public static class MarketRankCollectionListTypeRef extends TypeReference<List<MarketRankCollection>> {}
    public static class PricedStockResultsTypeRef extends TypeReference<Results<PricedStock>> {}
    public static class PricedStockListTypeRef extends TypeReference<List<PricedStock>> {}
    public static class PricedUserStockResultsTypeRef extends TypeReference<Results<PricedUserStock>> {}
    public static class RankedUserResultsTypeRef extends TypeReference<Results<RankedUser>> {}
    public static class StockPriceChangeResultsTypeRef extends TypeReference<Results<StockPriceChange>> {}
    public static class UserAchievementListTypeRef extends TypeReference<List<UserAchievement>> {}
}
