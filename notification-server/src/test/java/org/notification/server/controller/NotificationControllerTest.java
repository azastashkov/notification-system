package org.notification.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.notification.server.dto.SendNotificationRequest;
import org.notification.server.dto.SendNotificationResponse;
import org.notification.server.model.NotificationStatus;
import org.notification.server.model.NotificationType;
import org.notification.server.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendNotification_validRequest_returns200() throws Exception {
        SendNotificationRequest request = new SendNotificationRequest(1L, NotificationType.EMAIL, "Title", "Body", null, null);
        SendNotificationResponse response = new SendNotificationResponse(1L, NotificationStatus.QUEUED, "Notification queued");
        when(notificationService.send(any())).thenReturn(response);

        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationId").value(1))
                .andExpect(jsonPath("$.status").value("QUEUED"));
    }

    @Test
    void sendNotification_invalidRequest_returns400() throws Exception {
        // Missing required fields
        String invalidJson = "{\"userId\": null, \"type\": \"EMAIL\", \"body\": \"\"}";

        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendNotification_validationError_returns400() throws Exception {
        SendNotificationRequest request = new SendNotificationRequest(1L, NotificationType.EMAIL, "Title", "Body", null, null);
        when(notificationService.send(any())).thenThrow(new IllegalArgumentException("Invalid email format"));

        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
