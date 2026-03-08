package org.notification.loadclient.service;

import org.notification.loadclient.model.Device;
import org.notification.loadclient.model.NotificationTemplate;
import org.notification.loadclient.model.User;
import org.notification.loadclient.model.UserSettings;
import org.notification.loadclient.repository.DeviceRepository;
import org.notification.loadclient.repository.NotificationTemplateRepository;
import org.notification.loadclient.repository.UserRepository;
import org.notification.loadclient.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final NotificationTemplateRepository templateRepository;
    private final UserSettingsRepository userSettingsRepository;

    public List<User> seedUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            User user = User.builder()
                    .name("User " + i)
                    .email("user" + i + "@example.com")
                    .phoneNumber("+1555" + String.format("%07d", i))
                    .build();
            users.add(user);
        }
        users = userRepository.saveAll(users);
        log.info("Seeded {} users", users.size());
        return users;
    }

    public void seedDevices(List<User> users) {
        List<Device> devices = new ArrayList<>();
        for (User user : users) {
            devices.add(Device.builder()
                    .userId(user.getId())
                    .deviceToken("ios-token-" + UUID.randomUUID())
                    .platform("IOS")
                    .active(true)
                    .build());
            if (user.getId() % 2 == 0) {
                devices.add(Device.builder()
                        .userId(user.getId())
                        .deviceToken("android-token-" + UUID.randomUUID())
                        .platform("ANDROID")
                        .active(true)
                        .build());
            }
        }
        deviceRepository.saveAll(devices);
        log.info("Seeded {} devices", devices.size());
    }

    public void seedTemplates() {
        List<NotificationTemplate> templates = List.of(
                NotificationTemplate.builder().name("welcome").type("EMAIL")
                        .titleTemplate("Welcome {{name}}!")
                        .bodyTemplate("Hello {{name}}, welcome to our platform!").build(),
                NotificationTemplate.builder().name("welcome").type("SMS")
                        .titleTemplate("Welcome")
                        .bodyTemplate("Welcome {{name}}! Reply STOP to unsubscribe.").build(),
                NotificationTemplate.builder().name("alert").type("APNS")
                        .titleTemplate("Alert: {{title}}")
                        .bodyTemplate("{{message}}").build(),
                NotificationTemplate.builder().name("alert").type("FCM")
                        .titleTemplate("Alert: {{title}}")
                        .bodyTemplate("{{message}}").build()
        );
        templateRepository.saveAll(templates);
        log.info("Seeded {} templates", templates.size());
    }

    public void seedUserSettings(List<User> users) {
        List<UserSettings> settings = new ArrayList<>();
        for (User user : users) {
            settings.add(UserSettings.builder()
                    .userId(user.getId())
                    .emailEnabled(true)
                    .smsEnabled(true)
                    .pushEnabled(true)
                    .build());
        }
        userSettingsRepository.saveAll(settings);
        log.info("Seeded {} user settings", settings.size());
    }
}
