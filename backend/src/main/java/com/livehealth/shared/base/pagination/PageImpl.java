package com.livehealth.shared.base.pagination;

import java.util.List;

public class PageImpl<T> implements Page<T> {
    private final List<T> content;
    private final long total;
    private final Pageable pageable;

    public PageImpl(List<T> content, Pageable pageable, long total) {
        this.content = content;
        this.pageable = pageable;
        this.total = total;
    }

    @Override
    public List<T> getContent() { return content; }

    @Override
    public long getTotalElements() { return total; }

    @Override
    public int getTotalPages() {
        return pageable.getPageSize() == 0 ? 1 : (int) Math.ceil((double) total / pageable.getPageSize());
    }
}
