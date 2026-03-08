package com.notification.server.service;

import com.notification.server.dto.NotificationEvent;
import com.notification.server.dto.SendNotificationRequest;
import com.notification.server.dto.SendNotificationResponse;
import com.notification.server.model.*;
import com.notification.server.repository.NotificationRequestRepository;
import com.notification.server.validation.NotificationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final UserService userService;
    private final DeviceService deviceService;
    private final TemplateService templateService;
    private final NotificationValidator validator;
    private final NotificationRequestRepository requestRepository;
    private final MessagePublisher messagePublisher;

    public SendNotificationResponse send(SendNotificationRequest request) {
        User user = userService.getUser(request.userId());
        List<Device> devices = deviceService.getActiveDevices(request.userId());

        validator.validate(request, user, devices);

        String title = request.title();
        String body = request.body();

        if (request.templateName() != null) {
            Optional<NotificationTemplate> templateOpt =
                    templateService.getTemplate(request.templateName(), request.type());
            if (templateOpt.isPresent()) {
                NotificationTemplate template = templateOpt.get();
                title = templateService.renderTemplate(template.getTitleTemplate(), request.templateParams());
                body = templateService.renderTemplate(template.getBodyTemplate(), request.templateParams());
            }
        }

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .userId(request.userId())
                .type(request.type())
                .title(title)
                .body(body)
                .status(NotificationStatus.PENDING)
                .build();
        notificationRequest = requestRepository.save(notificationRequest);

        switch (request.type()) {
            case APNS, FCM -> {
                for (Device device : devices) {
                    NotificationEvent event = NotificationEvent.builder()
                            .notificationId(notificationRequest.getId())
                            .userId(user.getId())
                            .type(request.type().name())
                            .title(title)
                            .body(body)
                            .recipient(device.getDeviceToken())
                            .build();
                    messagePublisher.publish(request.type(), event);
                }
            }
            case SMS -> {
                NotificationEvent event = NotificationEvent.builder()
                        .notificationId(notificationRequest.getId())
                        .userId(user.getId())
                        .type(request.type().name())
                        .title(title)
                        .body(body)
                        .recipient(user.getPhoneNumber())
                        .build();
                messagePublisher.publish(request.type(), event);
            }
            case EMAIL -> {
                NotificationEvent event = NotificationEvent.builder()
                        .notificationId(notificationRequest.getId())
                        .userId(user.getId())
                        .type(request.type().name())
                        .title(title)
                        .body(body)
                        .recipient(user.getEmail())
                        .build();
                messagePublisher.publish(request.type(), event);
            }
        }

        notificationRequest.setStatus(NotificationStatus.QUEUED);
        requestRepository.save(notificationRequest);

        log.info("Notification {} queued for user {} via {}", notificationRequest.getId(), user.getId(), request.type());

        return new SendNotificationResponse(
                notificationRequest.getId(),
                NotificationStatus.QUEUED,
                "Notification queued successfully"
        );
    }
}
