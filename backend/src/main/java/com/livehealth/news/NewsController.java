package com.livehealth.news;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.constant.UrlConstant;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.news.dto.request.news.CreateNewsRequestDto;
import com.livehealth.news.dto.request.news.UpdateNewsRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.news.dto.response.news.NewsResponseDto;
import com.livehealth.news.NewsService;
import com.livehealth.shared.base.HttpStatus;
import com.livehealth.shared.base.MultipartFile;
import com.livehealth.shared.base.QuarkusMultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;
import java.util.UUID;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsController {

    NewsService newsService;

    // ==================== PUBLIC ====================

    @Operation(summary = "Lấy danh sách tin tức", description = "Tìm danh sách tin tức với bộ lọc theo category hoặc tag")
    @GET
    @Path(UrlConstant.News.GET_ALL_NEWS)
    public Response getAllNews(
            @Parameter(description = "ID danh mục tin tức (không bắt buộc)") @QueryParam("categoryId") UUID categoryId,
            @Parameter(description = "ID tag tin tức (không bắt buộc)") @QueryParam("tagId") UUID tagId,
            @BeanParam PaginationRequestDto request) {
        PaginationResponseDto<NewsResponseDto> response = newsService.getAllNews(categoryId, tagId, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Lấy chi tiết tin tức", description = "Lấy chi tiết bài viết dựa trên ID")
    @GET
    @Path(UrlConstant.News.GET_NEWS_BY_ID)
    public Response getNewsById(@PathParam("id") UUID id) {
        NewsResponseDto response = newsService.getNewsById(id);
        return VsResponseUtil.success(response);
    }

    // ==================== ADMIN ====================

    @Operation(summary = "Tạo bài viết mới", description = "Tạo mới thông tin bài viết", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.News.CREATE_NEWS)
    public Response createNews(@Valid CreateNewsRequestDto request) {
        NewsResponseDto response = newsService.createNews(request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Cập nhật bài viết", description = "Chỉnh sửa tiêu đề, nội dung, danh mục cho bài viết", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path(UrlConstant.News.UPDATE_NEWS)
    public Response updateNews(
            @PathParam("id") UUID id,
            @Valid UpdateNewsRequestDto request) {
        NewsResponseDto response = newsService.updateNews(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Upload thumbnail", description = "Tải ảnh bìa (thumbnail) bài viết", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.News.UPLOAD_THUMBNAIL)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadThumbnail(
            @PathParam("id") UUID id,
            @RestForm("file") FileUpload file) {
        NewsResponseDto response = newsService.uploadThumbnail(id, new QuarkusMultipartFile(file));
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Upload ảnh mô tả trong bài viết", description = "Tải các ảnh (tối đa 2) để sử dụng ở trong nội dung bài viết", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.News.UPLOAD_IMAGES)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadImages(
            @PathParam("id") UUID id,
            @RestForm("files") List<FileUpload> files) {
        List<MultipartFile> multipartFiles = files.stream()
                .map(QuarkusMultipartFile::new)
                .collect(java.util.stream.Collectors.toList());
        NewsResponseDto response = newsService.uploadImages(id, multipartFiles);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa bài viết", description = "Admin chức năng xóa bài viết", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path(UrlConstant.News.DELETE_NEWS)
    public Response deleteNews(@PathParam("id") UUID id) {
        CommonResponseDto response = newsService.deleteNews(id);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Thêm tags vào tin tức", description = "Gắn danh sách tag cho tin tức", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.News.ADD_TAGS_TO_NEWS)
    public Response addTagsToNews(
            @PathParam("id") UUID id,
            List<UUID> tagIds) {
        NewsResponseDto response = newsService.addTagsToNews(id, tagIds);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa tag khỏi tin", description = "Gỡ bỏ 1 tag khỏi danh sách tag của tin", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path(UrlConstant.News.REMOVE_TAG_FROM_NEWS)
    public Response removeTagFromNews(
            @PathParam("id") UUID id,
            @PathParam("tagId") UUID tagId) {
        NewsResponseDto response = newsService.removeTagFromNews(id, tagId);
        return VsResponseUtil.success(response);
    }
}
