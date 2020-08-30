package vstocks.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Results<T> {
    private Page page = new Page();
    private List<T> results = new ArrayList<>();

    public Results() {
    }

    public Page getPage() {
        return page;
    }

    public Results<T> setPage(Page page) {
        this.page = page;
        return this;
    }

    public List<T> getResults() {
        return results;
    }

    public Results<T> setResults(List<T> results) {
        this.results = results;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Results<?> results1 = (Results<?>) o;
        return Objects.equals(page, results1.page) &&
                Objects.equals(results, results1.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, results);
    }

    @Override
    public String toString() {
        return "Results{" +
                "page=" + page +
                ", results=" + results +
                '}';
    }
}
