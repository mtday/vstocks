package vstocks.rest.resource;

import org.pac4j.core.profile.CommonProfile;
import vstocks.model.Page;
import vstocks.model.Sort;
import vstocks.model.User;

import javax.ws.rs.core.SecurityContext;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static vstocks.model.User.generateId;

public abstract class BaseResource {
    public static final Pattern VALID_DISPLAY_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9 _'-]+");
    public static final String INVALID_DISPLAY_NAME_MESSAGE =
            "The specified name contains invalid characters. Only alphanumeric characters, along with underscores, " +
                    "dashes, and single quote characters are allowed.";

    public static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("[a-zA-Z0-9_-]+");
    public static final String INVALID_USERNAME_MESSAGE =
            "The specified username contains invalid characters. Only alphanumeric characters, along with " +
                    "underscores and dashes are allowed.";

    public static final String USERNAME_EXISTS_MESSAGE = "The specified username is already taken.";

    private static final int DEFAULT_PAGE_SIZE = 25;
    private static final int MAX_PAGE_SIZE = 50;

    protected Page getPage(Integer pageNum, Integer pageSize) {
        return new Page()
                .setPage(ofNullable(pageNum).orElse(1))
                .setSize(ofNullable(pageSize).map(size -> Math.min(MAX_PAGE_SIZE, size)).orElse(DEFAULT_PAGE_SIZE));
    }

    protected List<Sort> getSort(String sortConfig) {
        return Stream.of(sortConfig)
                .filter(Objects::nonNull)
                .map(sort -> sort.split(","))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .filter(sort -> !sort.isEmpty())
                .map(Sort::parse)
                .collect(toList());
    }

    protected User getUser(SecurityContext securityContext) {
        return Stream.of(securityContext)
                .map(SecurityContext::getUserPrincipal)
                .filter(principal -> principal instanceof User)
                .map(principal -> (User) principal)
                .findFirst()
                .orElse(null);
    }

    protected User getUser(CommonProfile commonProfile) {
        return ofNullable(commonProfile).map(profile -> {
            String profileImage = Stream.of("profile_url", "picture_url", "profile_image_url_https", "profile_image_url")
                    .map(commonProfile::getAttribute)
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .findFirst()
                    .orElse(null);
            return new User()
                    .setId(generateId(profile.getEmail()))
                    .setEmail(profile.getEmail())
                    .setUsername(profile.getUsername())
                    .setDisplayName(profile.getDisplayName())
                    .setProfileImage(profileImage);
        }).orElse(null);
    }
}
