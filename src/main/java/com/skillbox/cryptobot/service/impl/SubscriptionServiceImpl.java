package com.skillbox.cryptobot.service.impl;

import com.skillbox.cryptobot.entity.SubscribersEntity;
import com.skillbox.cryptobot.repositories.SubscribersRepository;
import com.skillbox.cryptobot.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;

@RequiredArgsConstructor
@Service
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscribersRepository subscribersRepository;

    @Override
    public boolean existsById(Message message) {
        String userId = getUserId(message);

        boolean exists = subscribersRepository.existsByTelegramUserId(userId);

        if (exists) {
            log.info("User {} already exists in DB", userId);
        }

        return exists;
    }

    @Override
    @Transactional
    public void save(Message message) {
        String userId = getUserId(message);

        SubscribersEntity subscribersEntity = new SubscribersEntity();
        subscribersEntity.setTelegramUserId(userId);
        subscribersEntity.setChatId(message.getChatId());

        subscribersRepository.save(subscribersEntity);
        log.info("User {} save in DB with chatID: {}", subscribersEntity.getTelegramUserId(), subscribersEntity.getChatId());
    }

    @Override
    @Transactional
    public void subscribe(Message message, String price) {
        String userId = getUserId(message);

        SubscribersEntity subscribersEntity = subscribersRepository.findByTelegramUserId(userId).orElseThrow(
                () -> new RuntimeException("User " + userId + " does not exist")
        );

        subscribersEntity.setPrice(Integer.parseInt(price));
        subscribersEntity.setLastNotificationTime(null);

        subscribersRepository.save(subscribersEntity);
        log.info("Update price {} from user {}", price, userId);
    }

    @Override
    public Integer getSubscribe(Message message) {
        String userId = getUserId(message);

        Integer price = subscribersRepository.findPriceByTelegramUserId(userId);
        log.info("Get price {} from user {}", price, userId);

        return price;
    }

    @Override
    @Transactional
    public boolean unsubscribe(Message message) {
        String userId = getUserId(message);

        SubscribersEntity subscribersEntity = subscribersRepository.findByTelegramUserId(userId).orElseThrow(
                () -> new RuntimeException("User " + userId + " does not exist")
        );

        if (subscribersEntity.getPrice() != null) {
            subscribersEntity.setPrice(null);
            log.info("User {} unsubscribe. Price equals {}", userId, subscribersEntity.getPrice());
            subscribersRepository.save(subscribersEntity);
            return true;
        } else {
            log.info("User {} price is null", userId);
            return false;
        }
    }

    private String getUserId(Message message) {
        return message.getFrom().getId().toString();
    }
}
