package com.livehealth.shared.web;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.base.HttpStatus;
import com.livehealth.shared.base.pagination.Page;
import com.livehealth.shared.base.pagination.PageRequest;
import com.livehealth.shared.base.pagination.Pageable;
import com.livehealth.shared.base.pagination.Sort;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.dto.pagination.PagingMeta;
import com.livehealth.shared.exception.VsException;
import com.livehealth.shared.web.dto.request.ContactReplyRequestDto;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestApiV1
@Path("/api/v1/admin/contacts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminContactController {

    @Inject
    ContactMessageRepository contactMessageRepository;

    @Inject
    Mailer mailer;

    @ConfigProperty(name = "quarkus.mailer.username", defaultValue = "")
    String mailUsername;

    @Operation(summary = "Lấy danh sách tin nhắn liên hệ", security = @SecurityRequirement(name = "Bearer Token"))
    @GET
    public Response getAllContacts(@BeanParam PaginationRequestDto request) {
        Pageable pageable = PageRequest.of(
                request.getPageNum(),
                request.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<ContactMessage> contactPage = contactMessageRepository.findAll(pageable);

        PagingMeta pagingMeta = new PagingMeta(
                contactPage.getTotalElements(),
                contactPage.getTotalPages(),
                request.getPageNum() + 1,
                request.getPageSize(),
                null,
                null
        );

        return VsResponseUtil.success(new PaginationResponseDto<>(pagingMeta, contactPage.getContent()));
    }

    @Operation(summary = "Lấy chi tiết tin nhắn liên hệ", security = @SecurityRequirement(name = "Bearer Token"))
    @GET
    @Path("/{id}")
    public Response getContactById(@PathParam("id") UUID id) {
        ContactMessage contactMessage = contactMessageRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, "Không tìm thấy tin nhắn liên hệ"));
        return VsResponseUtil.success(contactMessage);
    }

    @Operation(summary = "Trả lời tin nhắn liên hệ", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path("/{id}/reply")
    @Transactional(rollbackOn = Exception.class)
    public Response replyToContact(@PathParam("id") UUID id, @Valid ContactReplyRequestDto request) {
        ContactMessage contactMessage = contactMessageRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, "Không tìm thấy tin nhắn liên hệ"));

        // Update database status
        contactMessage.setReply(request.getReply());
        contactMessage.setStatus("REPLIED");
        contactMessage.setRepliedAt(LocalDateTime.now());
        contactMessageRepository.save(contactMessage);

        // Send reply email to user
        String emailBody = String.format(
                "Kính gửi %s,\n\n" +
                "LiveHealth xin chân thành cảm ơn ý kiến đóng góp/phản hồi của bạn:\n" +
                "\"%s\"\n\n" +
                "Chúng tôi xin được phản hồi lại như sau:\n" +
                "%s\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ LiveHealth",
                contactMessage.getName(), contactMessage.getMessage(), request.getReply()
        );

        if (mailUsername != null && !mailUsername.isBlank()) {
            try {
                Mail mail = Mail.withText(
                        contactMessage.getEmail(),
                        "LiveHealth - Phản hồi liên hệ: " + contactMessage.getSubject(),
                        emailBody
                );
                mail.setFrom(mailUsername);
                mailer.send(mail);
                log.info("Successfully sent reply email to customer: {}", contactMessage.getEmail());
            } catch (Exception e) {
                log.warn("Failed to send contact reply email: {}", e.getMessage());
            }
        } else {
            log.info("Mailer username is empty. Skipping email reply sending.");
        }

        return VsResponseUtil.success(new CommonResponseDto(HttpStatus.OK, "Trả lời tin nhắn liên hệ thành công."));
    }
}
