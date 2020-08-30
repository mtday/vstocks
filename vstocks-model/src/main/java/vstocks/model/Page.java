package vstocks.model;

import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.empty;

public class Page {
    private Integer page;
    private Integer size;
    private Integer totalPages;
    private Integer firstRow;
    private Integer lastRow;
    private Integer totalRows;

    public Page() {
        page = 1;
        size = 20;
    }

    public static Page from(int page, int size, int rowsInPage, int totalRows) {
        return new Page()
                .setPage(page)
                .setSize(size)
                .setTotalPages(totalRows == 0 ? 0 : (totalRows / size) + (totalRows % size == 0 ? 0 : 1))
                .setFirstRow(rowsInPage == 0 ? null : (page - 1) * size + 1)
                .setLastRow(rowsInPage == 0 ? null : (page - 1) * size + rowsInPage)
                .setTotalRows(totalRows);
    }

    public Optional<Page> previous() {
        if (firstRow == null || firstRow == 1) {
            return empty();
        }
        Page page = new Page()
                .setPage(this.page - 1)
                .setSize(size)
                .setTotalPages(totalPages)
                .setFirstRow(Math.max(1, firstRow - size))
                .setLastRow(Math.max(1, firstRow - size) - 1 + size)
                .setTotalRows(totalRows);
        return Optional.of(page);
    }

    public Optional<Page> next() {
        if (lastRow == null || Objects.equals(lastRow, totalRows)) {
            return empty();
        }
        Page page = new Page()
                .setPage(this.page + 1)
                .setSize(size)
                .setTotalPages(totalPages)
                .setFirstRow(lastRow + 1)
                .setLastRow(Math.min(totalRows, lastRow + size))
                .setTotalRows(totalRows);
        return Optional.of(page);
    }

    public Integer getPage() {
        return page;
    }

    public Page setPage(Integer page) {
        this.page = page;
        return this;
    }

    public Integer getSize() {
        return size;
    }

    public Page setSize(Integer size) {
        this.size = size;
        return this;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public Page setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public Integer getFirstRow() {
        return firstRow;
    }

    public Page setFirstRow(Integer firstRow) {
        this.firstRow = firstRow;
        return this;
    }

    public Integer getLastRow() {
        return lastRow;
    }

    public Page setLastRow(Integer lastRow) {
        this.lastRow = lastRow;
        return this;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public Page setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page p = (Page) o;
        return Objects.equals(page, p.page) &&
                Objects.equals(size, p.size) &&
                Objects.equals(totalPages, p.totalPages) &&
                Objects.equals(firstRow, p.firstRow) &&
                Objects.equals(lastRow, p.lastRow) &&
                Objects.equals(totalRows, p.totalRows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, size, totalPages, firstRow, lastRow, totalRows);
    }

    @Override
    public String toString() {
        return "Page{" +
                "page=" + page +
                ", size=" + size +
                ", totalPages=" + totalPages +
                ", firstRow=" + firstRow +
                ", lastRow=" + lastRow +
                ", totalRows=" + totalRows +
                '}';
    }
}
