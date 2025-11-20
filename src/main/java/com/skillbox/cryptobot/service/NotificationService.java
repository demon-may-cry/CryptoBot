package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.entity.SubscribersEntity;

public interface NotificationService {

    void checkPrice();

    void sendNotification(SubscribersEntity subscribers, Integer currentPrice);

}
