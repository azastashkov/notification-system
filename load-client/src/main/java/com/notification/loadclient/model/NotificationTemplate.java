package com.notification.loadclient.model;

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
    private String type;
    @Column(columnDefinition = "TEXT")
    private String titleTemplate;
    @Column(columnDefinition = "TEXT")
    private String bodyTemplate;
}
