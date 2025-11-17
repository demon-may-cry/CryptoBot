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
        if (subscribersRepository.existsById(getUserName(message))) {
            log.info("User {} already exist in DB", getUserName(message));
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void save(Message message) {
        SubscribersEntity subscribersEntity = new SubscribersEntity();
        subscribersEntity.setId(getUserName(message));

        subscribersRepository.save(subscribersEntity);
        log.info("User {} save in DB", subscribersEntity.getId());
    }

    @Override
    @Transactional
    public void subscribe(Message message, String price) {
        SubscribersEntity subscribersEntity = subscribersRepository.findById(getUserName(message));
        subscribersEntity.setPrice(Integer.parseInt(price));

        subscribersRepository.save(subscribersEntity);
        log.info("Update price {} from user {}", price, getUserName(message));
    }

    @Override
    public Integer getSubscribe(Message message) {
        Integer price = subscribersRepository.findPriceById(getUserName(message));
        log.info("Get price {} from user {}", price, getUserName(message));
        return price;
    }

    @Override
    @Transactional
    public boolean unsubscribe(Message message) {
        SubscribersEntity subscribersEntity = subscribersRepository.findById(getUserName(message));
        if (subscribersEntity.getPrice() != null) {
            subscribersEntity.setPrice(null);
            log.info("User {} unsubscribe. Price equals {}", getUserName(message), subscribersEntity.getPrice());
            return true;
        } else {
            log.info("User {} price is null", getUserName(message));
            return false;
        }
    }

    private String getUserName(Message message) {
        return message.getFrom().getUserName() == null ?
                String.valueOf(message.getFrom().getId()) : message.getFrom().getUserName();
    }
}
