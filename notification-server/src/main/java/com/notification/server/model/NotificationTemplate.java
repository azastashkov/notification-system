package com.notification.server.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(columnDefinition = "TEXT")
    private String titleTemplate;

    @Column(columnDefinition = "TEXT")
    private String bodyTemplate;
}
