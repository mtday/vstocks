package vstocks.rest.resource;

import vstocks.model.Page;

import static java.util.Optional.ofNullable;

public abstract class BaseResource {
    private static final int DEFAULT_PAGE_SIZE = 25;

    protected Page getPage(Integer pageNum, Integer pageSize) {
        return new Page()
                .setPage(ofNullable(pageNum).orElse(1))
                .setSize(ofNullable(pageSize).orElse(DEFAULT_PAGE_SIZE));
    }
}
