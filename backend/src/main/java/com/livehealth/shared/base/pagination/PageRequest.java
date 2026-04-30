package com.livehealth.shared.base.pagination;

public class PageRequest implements Pageable {
    private final int page;
    private final int size;
    private final Sort sort;

    protected PageRequest(int page, int size, Sort sort) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size, Sort.unsorted());
    }

    public static PageRequest of(int page, int size, Sort sort) {
        return new PageRequest(page, size, sort);
    }

    @Override
    public int getPageNumber() { return page; }

    @Override
    public int getPageSize() { return size; }

    @Override
    public long getOffset() { return (long) page * size; }

    @Override
    public Sort getSort() { return sort; }
}
