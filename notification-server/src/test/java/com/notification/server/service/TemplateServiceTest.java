package com.notification.server.service;

import com.notification.server.model.NotificationTemplate;
import com.notification.server.model.NotificationType;
import com.notification.server.repository.NotificationTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    private NotificationTemplateRepository templateRepository;

    @InjectMocks
    private TemplateService templateService;

    @Test
    void getTemplate_exists_returnsTemplate() {
        NotificationTemplate template = NotificationTemplate.builder()
                .id(1L).name("welcome").type(NotificationType.EMAIL)
                .titleTemplate("Welcome").bodyTemplate("Hello").build();
        when(templateRepository.findByNameAndType("welcome", NotificationType.EMAIL)).thenReturn(Optional.of(template));

        Optional<NotificationTemplate> result = templateService.getTemplate("welcome", NotificationType.EMAIL);

        assertTrue(result.isPresent());
        assertEquals("welcome", result.get().getName());
    }

    @Test
    void getTemplate_notExists_returnsEmpty() {
        when(templateRepository.findByNameAndType("unknown", NotificationType.EMAIL)).thenReturn(Optional.empty());

        Optional<NotificationTemplate> result = templateService.getTemplate("unknown", NotificationType.EMAIL);

        assertTrue(result.isEmpty());
    }

    @Test
    void renderTemplate_replacesPlaceholders() {
        String template = "Hello {{name}}, your order {{orderId}} is ready";
        Map<String, String> params = Map.of("name", "John", "orderId", "12345");

        String result = templateService.renderTemplate(template, params);

        assertEquals("Hello John, your order 12345 is ready", result);
    }

    @Test
    void renderTemplate_noPlaceholders_returnsOriginal() {
        String template = "Hello World";
        String result = templateService.renderTemplate(template, Map.of());
        assertEquals("Hello World", result);
    }

    @Test
    void renderTemplate_missingParam_leavesPlaceholder() {
        String template = "Hello {{name}}";
        String result = templateService.renderTemplate(template, Map.of());
        assertEquals("Hello {{name}}", result);
    }
}
