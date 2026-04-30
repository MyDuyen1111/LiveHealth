package com.livehealth.shared.dto.pagination;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationResponseDto<T> {

    PagingMeta meta;
    List<T> items;

    public PaginationResponseDto(PagingMeta meta, List<T> items) {
        this.meta = meta;

        if (items == null) {
            this.items = null;
        } else {
            this.items = Collections.unmodifiableList(items);
        }
    }

    public List<T> getItems() {
        return items == null ? null : new ArrayList<>(items);
    }

}
