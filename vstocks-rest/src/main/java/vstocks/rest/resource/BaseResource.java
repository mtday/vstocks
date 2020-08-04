package vstocks.rest.resource;

import org.pac4j.core.profile.CommonProfile;
import vstocks.model.Page;
import vstocks.model.Sort;
import vstocks.model.User;
import vstocks.model.UserSource;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;

public abstract class BaseResource {
    private static final int DEFAULT_PAGE_SIZE = 25;

    protected Page getPage(Integer pageNum, Integer pageSize) {
        return new Page()
                .setPage(ofNullable(pageNum).orElse(1))
                .setSize(ofNullable(pageSize).orElse(DEFAULT_PAGE_SIZE));
    }

    protected Set<Sort> getSort(String sortConfig) {
        return Stream.of(sortConfig)
                .filter(Objects::nonNull)
                .map(sort -> sort.split(","))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .filter(sort -> !sort.isEmpty())
                .map(Sort::parse)
                .collect(toCollection(LinkedHashSet::new));
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
