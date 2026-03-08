package com.notification.server.validation;

import com.notification.server.dto.SendNotificationRequest;
import com.notification.server.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationValidatorTest {

    private NotificationValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NotificationValidator();
    }

    @Test
    void validateEmail_validEmail_noException() {
        User user = User.builder().id(1L).email("test@example.com").phoneNumber("+1234567890").name("Test").build();
        SendNotificationRequest request = new SendNotificationRequest(1L, NotificationType.EMAIL, "Title", "Body", null, null);
        assertDoesNotThrow(() -> validator.validate(request, user, Collections.emptyList()));
    }

    @Test
    void validateEmail_invalidEmail_throwsException() {
        User user = User.builder().id(1L).email("invalid-email").phoneNumber("+1234567890").name("Test").build();
        SendNotificationRequest request = new SendNotificationRequest(1L, NotificationType.EMAIL, "Title", "Body", null, null);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request, user, Collections.emptyList()));
    }

    @Test
    void validateEmail_nullEmail_throwsException() {
        User user = User.builder().id(1L).email(null).phoneNumber("+1234567890").name("Test").build();
        SendNotificationRequest request = new SendNotificationRequest(1L, NotificationType.EMAIL, "Title", "Body", null, null);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request, user, Collections.emptyList()));
    }

    @Test
    void validateSms_validPhone_noException() {
        User user = User.builder().id(1L).email("test@example.com").phoneNumber("+1234567890").name("Test").build();
        SendNotificationRequest request = new SendNotificationRequest(1L, NotificationType.SMS, "Title", "Body", null, null);
        assertDoesNotThrow(() -> validator.validate(request, user, Collections.emptyList()));
    }

    @Test
    void validateSms_invalidPhone_throwsException() {
        User user = User.builder().id(1L).email("test@example.com").phoneNumber("not-a-phone").name("Test").build();
        SendNotificationRequest request = new SendNotificationRequest(1L, NotificationType.SMS, "Title", "Body", null, null);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request, user, Collections.emptyList()));
    }

    @Test
    void validateApns_withActiveDevices_noException() {
        User user = User.builder().id(1L).email("test@example.com").phoneNumber("+1234567890").name("Test").build();
        Device device = Device.builder().id(1L).userId(1L).deviceToken("token123").platform("IOS").active(true).build();
        SendNotificationRequest request = new SendNotificationRequest(1L, NotificationType.APNS, "Title", "Body", null, null);
        assertDoesNotThrow(() -> validator.validate(request, user, List.of(device)));
    }

    @Test
    void validateApns_noActiveDevices_throwsException() {
        User user = User.builder().id(1L).email("test@example.com").phoneNumber("+1234567890").name("Test").build();
        SendNotificationRequest request = new SendNotificationRequest(1L, NotificationType.APNS, "Title", "Body", null, null);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request, user, Collections.emptyList()));
    }

    @Test
    void validateFcm_withActiveDevices_noException() {
        User user = User.builder().id(1L).email("test@example.com").phoneNumber("+1234567890").name("Test").build();
        Device device = Device.builder().id(1L).userId(1L).deviceToken("fcm-token").platform("ANDROID").active(true).build();
        SendNotificationRequest request = new SendNotificationRequest(1L, NotificationType.FCM, "Title", "Body", null, null);
        assertDoesNotThrow(() -> validator.validate(request, user, List.of(device)));
    }
}
