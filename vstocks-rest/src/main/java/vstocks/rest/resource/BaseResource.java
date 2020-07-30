package vstocks.rest.resource;

import org.pac4j.core.profile.CommonProfile;
import vstocks.model.Page;
import vstocks.model.User;
import vstocks.model.UserSource;

import static java.util.Optional.ofNullable;

public abstract class BaseResource {
    private static final int DEFAULT_PAGE_SIZE = 25;

    protected Page getPage(Integer pageNum, Integer pageSize) {
        return new Page()
                .setPage(ofNullable(pageNum).orElse(1))
                .setSize(ofNullable(pageSize).orElse(DEFAULT_PAGE_SIZE));
    }

    protected User getUser(CommonProfile commonProfile) {
        return ofNullable(commonProfile).flatMap(profile ->
                UserSource.fromClientName(profile.getClientName()).map(userSource ->
                        new User()
                                .setId(userSource.getAbbreviation() + ":" + profile.getId())
                                .setUsername(profile.getUsername())
                                .setSource(userSource)
                                .setDisplayName(profile.getDisplayName())
                )
        ).orElse(null);
    }
}
