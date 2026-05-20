package com.livehealth.auth;

import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.exception.VsException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.livehealth.shared.base.HttpStatus;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailServiceImpl implements EmailService {

    Mailer mailer;

    @Inject
    @NonFinal
    @ConfigProperty(name = "quarkus.mailer.username", defaultValue = "")
    String mailUsername;

    @Override
    public void sendOtpEmail(String email, String otp) {
        Mail mail = Mail.withText(email, "Xác thực OTP", "Mã OTP của bạn là: " + otp);
        if (mailUsername != null && !mailUsername.isBlank()) {
            mail.setFrom(mailUsername);
        }

        try {
            mailer.send(mail);
        } catch (Exception e) {
            log.warn("Failed to send OTP email to {}: {}", email, e.getMessage());
            log.debug("OTP email send failure details", e);
            throw new VsException(HttpStatus.SERVICE_UNAVAILABLE, ErrorMessage.Auth.ERR_OTP_EMAIL_SEND_FAILED);
        }
    }
}
