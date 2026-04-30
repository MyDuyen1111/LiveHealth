package com.livehealth.shared.dto.pagination;

import com.livehealth.shared.constant.CommonConstant;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.ws.rs.QueryParam;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationRequestDto {

    @QueryParam("pageNum")
    @Parameter(description = "Page you want to retrieve (0..N)")
    Integer pageNum = CommonConstant.ONE_INT_VALUE;

    @QueryParam("pageSize")
    @Parameter(description = "Number of records per page.")
    Integer pageSize = CommonConstant.PAGE_SIZE_DEFAULT;

    @QueryParam("categoryId")
    @Parameter(description = "Filter by category ID (optional)")
    UUID categoryId;

    @QueryParam("brandId")
    @Parameter(description = "Filter by brand ID (optional)")
    UUID brandId;

    @QueryParam("keyword")
    @Parameter(description = "Search keyword for product name (optional)")
    String keyword;

    @QueryParam("sortBy")
    @Parameter(description = "Field used for sorting (optional)")
    String sortBy;

    @QueryParam("sortType")
    @Parameter(description = "Sort direction: ASC or DESC (optional)")
    String sortType;

    public int getPageNum() {
        if (pageNum < 1) {
            pageNum = CommonConstant.ONE_INT_VALUE;
        }
        return pageNum - 1;
    }

    public int getPageSize() {
        if (pageSize < 1) {
            pageSize = CommonConstant.PAGE_SIZE_DEFAULT;
        }
        return pageSize;
    }
}
