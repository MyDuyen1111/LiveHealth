package com.livehealth.shared.dto.pagination;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PagingMeta {
    Long totalElement;
    Integer totalPages;
    Integer pageNum;
    Integer pageSize;
    String sortBy;
    String sortType;
}
