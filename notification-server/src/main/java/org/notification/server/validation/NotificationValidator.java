package org.notification.server.validation;

import org.notification.server.dto.SendNotificationRequest;
import org.notification.server.model.Device;
import org.notification.server.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class NotificationValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+[1-9]\\d{6,14}$");

    public void validate(SendNotificationRequest request, User user, List<Device> devices) {
        switch (request.type()) {
            case EMAIL -> validateEmail(user);
            case SMS -> validatePhone(user);
            case APNS, FCM -> validateDevices(devices, request.type().name());
        }
    }

    private void validateEmail(User user) {
        if (user.getEmail() == null || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + user.getEmail());
        }
    }

    private void validatePhone(User user) {
        if (user.getPhoneNumber() == null || !PHONE_PATTERN.matcher(user.getPhoneNumber()).matches()) {
            throw new IllegalArgumentException("Invalid phone number format: " + user.getPhoneNumber());
        }
    }

    private void validateDevices(List<Device> devices, String type) {
        if (devices == null || devices.isEmpty()) {
            throw new IllegalArgumentException("No active devices found for " + type + " notification");
        }
    }
}
