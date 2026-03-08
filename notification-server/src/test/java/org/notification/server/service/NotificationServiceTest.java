package org.notification.server.service;

import org.notification.server.dto.NotificationEvent;
import org.notification.server.dto.SendNotificationRequest;
import org.notification.server.dto.SendNotificationResponse;
import org.notification.server.model.Device;
import org.notification.server.model.NotificationRequest;
import org.notification.server.model.NotificationStatus;
import org.notification.server.model.NotificationTemplate;
import org.notification.server.model.NotificationType;
import org.notification.server.model.User;
import org.notification.server.repository.NotificationRequestRepository;
import org.notification.server.validation.NotificationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private UserService userService;
    @Mock private DeviceService deviceService;
    @Mock private TemplateService templateService;
    @Mock private NotificationValidator validator;
    @Mock private NotificationRequestRepository requestRepository;
    @Mock private MessagePublisher messagePublisher;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).email("test@example.com").phoneNumber("+1234567890").name("Test User").build();
    }

    @Test
    void sendEmailNotification_success() {
        SendNotificationRequest request = new SendNotificationRequest(1L, NotificationType.EMAIL, "Title", "Body", null, null);
        when(userService.getUser(1L)).thenReturn(testUser);
        when(deviceService.getActiveDevices(1L)).thenReturn(Collections.emptyList());
        doNothing().when(validator).validate(any(), any(), any());
        when(requestRepository.save(any())).thenAnswer(inv -> {
            NotificationRequest nr = inv.getArgument(0);
            nr.setId(1L);
            return nr;
        });

        SendNotificationResponse response = notificationService.send(request);

        assertNotNull(response);
        assertEquals(NotificationStatus.QUEUED, response.status());
        verify(messagePublisher).publish(eq(NotificationType.EMAIL), any(NotificationEvent.class));
    }

    @Test
    void sendSmsNotification_success() {
        SendNotificationRequest request = new SendNotificationRequest(1L, NotificationType.SMS, "Title", "Body", null, null);
        when(userService.getUser(1L)).thenReturn(testUser);
        when(deviceService.getActiveDevices(1L)).thenReturn(Collections.emptyList());
        doNothing().when(validator).validate(any(), any(), any());
        when(requestRepository.save(any())).thenAnswer(inv -> {
            NotificationRequest nr = inv.getArgument(0);
            nr.setId(2L);
            return nr;
        });

        SendNotificationResponse response = notificationService.send(request);

        assertNotNull(response);
        assertEquals(NotificationStatus.QUEUED, response.status());
        verify(messagePublisher).publish(eq(NotificationType.SMS), any(NotificationEvent.class));
    }

    @Test
    void sendApnsNotification_success() {
        SendNotificationRequest request = new SendNotificationRequest(1L, NotificationType.APNS, "Title", "Body", null, null);
        Device device = Device.builder().id(1L).userId(1L).deviceToken("token123").platform("IOS").active(true).build();
        when(userService.getUser(1L)).thenReturn(testUser);
        when(deviceService.getActiveDevices(1L)).thenReturn(List.of(device));
        doNothing().when(validator).validate(any(), any(), any());
        when(requestRepository.save(any())).thenAnswer(inv -> {
            NotificationRequest nr = inv.getArgument(0);
            nr.setId(3L);
            return nr;
        });

        SendNotificationResponse response = notificationService.send(request);

        assertNotNull(response);
        assertEquals(NotificationStatus.QUEUED, response.status());
        verify(messagePublisher).publish(eq(NotificationType.APNS), any(NotificationEvent.class));
    }

    @Test
    void sendNotification_userNotFound_throwsException() {
        SendNotificationRequest request = new SendNotificationRequest(999L, NotificationType.EMAIL, "Title", "Body", null, null);
        when(userService.getUser(999L)).thenThrow(new IllegalArgumentException("User not found"));

        assertThrows(IllegalArgumentException.class, () -> notificationService.send(request));
        verify(messagePublisher, never()).publish(any(), any());
    }

    @Test
    void sendNotification_validationFails_throwsException() {
        SendNotificationRequest request = new SendNotificationRequest(1L, NotificationType.EMAIL, "Title", "Body", null, null);
        when(userService.getUser(1L)).thenReturn(testUser);
        when(deviceService.getActiveDevices(1L)).thenReturn(Collections.emptyList());
        doThrow(new IllegalArgumentException("Invalid email")).when(validator).validate(any(), any(), any());

        assertThrows(IllegalArgumentException.class, () -> notificationService.send(request));
        verify(messagePublisher, never()).publish(any(), any());
    }

    @Test
    void sendNotificationWithTemplate_success() {
        SendNotificationRequest request = new SendNotificationRequest(1L, NotificationType.EMAIL, null, null, "welcome", Map.of("name", "John"));
        NotificationTemplate template = NotificationTemplate.builder()
            .id(1L).name("welcome").type(NotificationType.EMAIL)
            .titleTemplate("Welcome {{name}}").bodyTemplate("Hello {{name}}, welcome!").build();
        when(userService.getUser(1L)).thenReturn(testUser);
        when(deviceService.getActiveDevices(1L)).thenReturn(Collections.emptyList());
        when(templateService.getTemplate("welcome", NotificationType.EMAIL)).thenReturn(Optional.of(template));
        when(templateService.renderTemplate(eq("Welcome {{name}}"), any())).thenReturn("Welcome John");
        when(templateService.renderTemplate(eq("Hello {{name}}, welcome!"), any())).thenReturn("Hello John, welcome!");
        doNothing().when(validator).validate(any(), any(), any());
        when(requestRepository.save(any())).thenAnswer(inv -> {
            NotificationRequest nr = inv.getArgument(0);
            nr.setId(4L);
            return nr;
        });

        SendNotificationResponse response = notificationService.send(request);

        assertNotNull(response);
        assertEquals(NotificationStatus.QUEUED, response.status());
        verify(templateService).renderTemplate(eq("Welcome {{name}}"), any());
    }
}
