package com.skillbox.cryptobot.repositories;

import com.skillbox.cryptobot.entity.SubscribersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscribersRepository extends JpaRepository<SubscribersEntity, UUID> {

    boolean existsByTelegramUserId(String telegramUserId);

    Optional<SubscribersEntity> findByTelegramUserId(String telegramUserId);

    @Query("SELECT s.price FROM SubscribersEntity s " +
            "WHERE s.telegramUserId = :telegramUserId")
    Integer findPriceByTelegramUserId(String telegramUserId);

    @Query("SELECT s FROM SubscribersEntity s " +
            "WHERE s.price IS NOT NULL " +
            "AND s.price >= :currentPrice " +
            "AND (s.lastNotificationTime IS NULL OR s.lastNotificationTime <= :minNotificationTime)")
    List<SubscribersEntity> findSubscribersToNotify(@Param("currentPrice") Integer currentPrice,
                                                    @Param("minNotificationTime") LocalDateTime minNotificationTime);

}
