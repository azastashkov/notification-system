package com.notification.server.service;

import com.notification.server.model.NotificationTemplate;
import com.notification.server.model.NotificationType;
import com.notification.server.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final NotificationTemplateRepository templateRepository;

    @Cacheable("templates")
    public Optional<NotificationTemplate> getTemplate(String name, NotificationType type) {
        return templateRepository.findByNameAndType(name, type);
    }

    public String renderTemplate(String template, Map<String, String> params) {
        if (template == null || params == null) {
            return template;
        }
        String result = template;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}
