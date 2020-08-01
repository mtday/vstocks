package vstocks.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class Page {
    private int page = 1;
    private int size = 25;

    public Page() {
    }

    @JsonIgnore
    public Page next() {
        return new Page().setPage(page + 1).setSize(size);
    }

    public int getPage() {
        return page;
    }

    public Page setPage(int page) {
        this.page = page;
        return this;
    }

    public int getSize() {
        return size;
    }

    public Page setSize(int size) {
        this.size = size;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page1 = (Page) o;
        return page == page1.page &&
                size == page1.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, size);
    }

    @Override
    public String toString() {
        return "Page{" +
                "page=" + page +
                ", size=" + size +
                '}';
    }
}
