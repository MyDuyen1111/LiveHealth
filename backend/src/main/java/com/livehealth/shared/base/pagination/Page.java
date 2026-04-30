package com.livehealth.shared.base.pagination;

import java.util.List;

public interface Page<T> {
    List<T> getContent();
    long getTotalElements();
    int getTotalPages();
}
