package com.skillbox.cryptobot.service;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface SubscriptionService {

    boolean existsById(Message message);

    void save(Message message);

    void subscribe(Message message, String price);

    Integer getSubscribe(Message message);

    boolean unsubscribe(Message message);
}
