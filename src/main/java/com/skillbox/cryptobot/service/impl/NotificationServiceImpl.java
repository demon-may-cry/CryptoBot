package com.skillbox.cryptobot.service.impl;

import com.skillbox.cryptobot.bot.CryptoBot;
import com.skillbox.cryptobot.entity.SubscribersEntity;
import com.skillbox.cryptobot.repositories.SubscribersRepository;
import com.skillbox.cryptobot.service.CryptoCurrencyService;
import com.skillbox.cryptobot.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final CryptoCurrencyService cryptoCurrencyService;
    private final SubscribersRepository subscribersRepository;
    private final CryptoBot cryptoBot;

    @Value("${telegram.bot.notify.delay.value}")
    private int notifyDelayValue;

    private static final DecimalFormat DF = new DecimalFormat("#,###.00");

    @Override
    @Transactional
    @Scheduled(fixedRateString = "${telegram.bot.price.check.interval}")
    public void checkPrice() {
        log.info("Starting check price and notification...");

        Integer currentPrice;
        try {
            currentPrice = (int) cryptoCurrencyService.getBitcoinPrice();
            log.info("Current price: {}", currentPrice);
        } catch (Exception ex) {
            log.error("Error while getting currency price", ex);
            return;
        }

        LocalDateTime minNotificationTime = LocalDateTime.now().minusMinutes(notifyDelayValue);
        log.info("Min notification time: {}", minNotificationTime);

        List<SubscribersEntity> subscribersToNotify = subscribersRepository
                .findSubscribersToNotify(currentPrice, minNotificationTime);
        log.info("Found {} subscribers to notify", subscribersToNotify.size());

        subscribersToNotify.forEach(subscriber -> sendNotification(subscriber, currentPrice));
    }

    @Override
    public void sendNotification(SubscribersEntity subscribers, Integer currentPrice) {
        try {
            cryptoBot.execute(SendMessage.builder()
                    .chatId(subscribers.getChatId())
                    .text(message(currentPrice))
                    .build());
        } catch (Exception ex) {
            log.error("Error while sending notification", ex);
            return;
        }

        subscribers.setLastNotificationTime(LocalDateTime.now());
        subscribersRepository.save(subscribers);

        log.info("Notification sent to user {} (target price = {} :: current price = {})",
                subscribers.getTelegramUserId(),
                subscribers.getPrice(),
                currentPrice);

    }

    private String message(Integer currentPrice) {
        return format("Пора покупать, стоимость биткоина %s", DF.format(currentPrice));
    }
}
