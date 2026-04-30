package com.livehealth.shared.base.pagination;

public interface Pageable {
    int getPageNumber();
    int getPageSize();
    long getOffset();
    Sort getSort();
}
