package com.skillbox.cryptobot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "subscribers")
public class SubscribersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "chat_id", nullable = false, unique = true)
    private Long chatId;

    @Column(name = "telegram_user_id", nullable = false,  unique = true)
    private String telegramUserId;

    @Column(name = "price")
    private Integer price;

    @Column(name = "last_notification_time")
    private LocalDateTime lastNotificationTime;

}