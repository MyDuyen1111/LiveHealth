package com.livehealth.news;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.constant.UrlConstant;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.news.dto.request.comment.NewsCommentRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.news.dto.response.comment.NewsCommentResponseDto;
import com.livehealth.news.NewsCommentService;
import com.livehealth.shared.base.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@RestApiV1
@Path("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsCommentController {

    NewsCommentService newsCommentService;

    // ==================== PUBLIC ====================

    @Operation(summary = "Lấy danh sách bình luận của bài viết", description = "Tìm các bình luận dựa theo ID bài viết")
    @GET
    @Path(UrlConstant.NewsComment.GET_ALL_COMMENTS)
    public Response getCommentsByNewsId(
            @PathParam("newsId") UUID newsId,
            @BeanParam PaginationRequestDto request) {
        PaginationResponseDto<NewsCommentResponseDto> response = newsCommentService.getCommentsByNewsId(newsId, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Lấy chi tiết bình luận", description = "Tìm chi tiết 1 bình luận theo ID")
    @GET
    @Path(UrlConstant.NewsComment.GET_COMMENT_BY_ID)
    public Response getCommentById(@PathParam("id") UUID id) {
        NewsCommentResponseDto response = newsCommentService.getCommentById(id);
        return VsResponseUtil.success(response);
    }

    // ==================== USER / ADMIN ====================

    @Operation(summary = "Tạo bình luận", description = "Người dùng đưa ra bình luận về bài viết", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.NewsComment.CREATE_COMMENT)
    public Response createComment(
            @PathParam("newsId") UUID newsId,
            @Valid NewsCommentRequestDto request) {
        NewsCommentResponseDto response = newsCommentService.createComment(newsId, request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Cập nhật bình luận", description = "Người dùng có thể sửa đổi nội dung bình luận của mình", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path(UrlConstant.NewsComment.UPDATE_COMMENT)
    public Response updateComment(
            @PathParam("id") UUID id,
            @Valid NewsCommentRequestDto request) {
        NewsCommentResponseDto response = newsCommentService.updateComment(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa bình luận", description = "Admin hoặc User tự xóa bình luận của mình", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path(UrlConstant.NewsComment.DELETE_COMMENT)
    public Response deleteComment(@PathParam("id") UUID id) {
        CommonResponseDto response = newsCommentService.deleteComment(id);
        return VsResponseUtil.success(response);
    }
}
