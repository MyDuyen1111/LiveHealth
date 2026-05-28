package com.livehealth.shared.web;

import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.shared.web.dto.request.ContactRequestDto;
import com.livehealth.shared.base.HttpStatus;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
@Path("/api/v1/contact")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ContactController {

    @Inject
    Mailer mailer;

    @ConfigProperty(name = "app.admin.email", defaultValue = "admin@livehealth.com")
    String adminEmail;

    @ConfigProperty(name = "quarkus.mailer.username", defaultValue = "")
    String mailUsername;

    @POST
    public Response sendContactMessage(@Valid ContactRequestDto request) {
        log.info("Received contact message from: {} <{}>. Subject: {}", 
                 request.getName(), request.getEmail(), request.getSubject());
        
        // Build email body
        String body = String.format(
            "Bạn nhận được một tin nhắn liên hệ mới từ website LiveHealth:\n\n" +
            "Họ tên: %s\n" +
            "Email: %s\n" +
            "Chủ đề: %s\n\n" +
            "Nội dung:\n%s\n",
            request.getName(), request.getEmail(), request.getSubject(), request.getMessage()
        );

        if (mailUsername != null && !mailUsername.isBlank()) {
            try {
                Mail mail = Mail.withText(adminEmail, "LiveHealth - Tin nhắn liên hệ mới: " + request.getSubject(), body);
                mail.setFrom(mailUsername);
                mailer.send(mail);
            } catch (Exception e) {
                log.warn("Failed to send contact email to admin: {}", e.getMessage());
            }
        } else {
            log.info("Mailer username is empty. Skipping email sending, but logging details.");
        }

        return VsResponseUtil.success(new CommonResponseDto(HttpStatus.OK, "Gửi tin nhắn liên hệ thành công."));
    }
}
