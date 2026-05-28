package com.livehealth;

import com.livehealth.cart.PaymentMethod;
import com.livehealth.cart.ShippingMethod;
import com.livehealth.order.PaymentMethodRepository;
import com.livehealth.order.ShippingMethodRepository;
import com.livehealth.user.UserRepository;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDate;
import com.livehealth.user.Role;
import com.livehealth.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;

@ApplicationScoped
@Slf4j
public class Application {

    @Inject
    UserRepository userRepository;
    @Inject
    PaymentMethodRepository paymentMethodRepository;
    @Inject
    ShippingMethodRepository shippingMethodRepository;

    @ConfigProperty(name = "app.admin.email")
    String adminEmail;
    @ConfigProperty(name = "app.admin.password")
    String adminPassword;
    @ConfigProperty(name = "app.admin.first-name")
    String adminFirstName;
    @ConfigProperty(name = "app.admin.last-name")
    String adminLastName;
    @ConfigProperty(name = "app.admin.phone")
    String adminPhone;

    @Inject
    PasswordEncoder passwordEncoder;

    @Transactional
    public void onStart(@Observes StartupEvent ev) {
        log.info("LiveHealth application is starting...");
        
        ensureTestAccount(
                adminEmail,
                adminPassword,
                adminFirstName,
                adminLastName,
                adminPhone,
                Role.ADMIN,
                passwordEncoder);

        ensureTestAccount(
                "admin@livehealth.vn",
                "Admin123@",
                "System",
                "Admin VN",
                "0901234567",
                Role.ADMIN,
                passwordEncoder);

        ensureTestAccount(
                "user@livehealth.vn",
                "User123@",
                "Khách",
                "LiveHealth",
                "0901234567",
                Role.USER,
                passwordEncoder);

        ensureTestAccount(
                "Duyeniuxinh@dangiu.embe",
                "embeiuu1111@",
                "Duyên",
                "Mỹ Duyên",
                "0987654321",
                Role.USER,
                passwordEncoder);

        ensurePaymentMethod("Thanh toán khi nhận hàng (COD)");
        ensurePaymentMethod("Chuyển khoản ngân hàng");
        ensurePaymentMethod("Ví MoMo");
        ensurePaymentMethod("VNPay");

        ensureShippingMethod("Giao hàng tiêu chuẩn", "Giao trong 3-5 ngày", 15000);
        ensureShippingMethod("Giao hàng nhanh", "Giao trong 1-2 ngày", 20000);
        ensureShippingMethod("Giao hàng hỏa tốc", "Giao trong 2 giờ (nội thành)", 30000);
        
        log.info("LiveHealth database initialization completed.");
    }

    private void ensureTestAccount(
            String email,
            String password,
            String firstName,
            String lastName,
            String phone,
            Role role,
            PasswordEncoder passwordEncoder) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> User.builder()
                        .email(email)
                        .createdAt(LocalDate.now())
                        .build());

        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setRole(role);

        userRepository.save(user);
        log.info("Test account is ready: {}", email);
    }

    private void ensurePaymentMethod(String name) {
        PaymentMethod paymentMethod = paymentMethodRepository.findFirstByName(name)
                .orElseGet(() -> PaymentMethod.builder()
                        .name(name)
                        .build());

        paymentMethod.setActive(true);
        paymentMethodRepository.save(paymentMethod);
        log.info("Payment method is ready: {}", name);
    }

    private void ensureShippingMethod(String name, String description, double price) {
        ShippingMethod shippingMethod = shippingMethodRepository.findFirstByName(name)
                .orElseGet(() -> ShippingMethod.builder()
                        .name(name)
                        .build());

        shippingMethod.setDescription(description);
        shippingMethod.setPrice(price);
        shippingMethod.setActive(true);
        shippingMethodRepository.save(shippingMethod);
        log.info("Shipping method is ready: {}", name);
    }
}
