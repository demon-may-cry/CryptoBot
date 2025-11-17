package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static java.lang.String.format;

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscribeCommand implements IBotCommand {

    private final SubscriptionService subscriptionService;
    private final GetPriceCommand getPriceCommand;

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());

        if (arguments.length == 0) {
            answer.setText("""
                    Укажите цену, на которую хотите подписаться
                    (пример: /subscribe 34600)
                    """);
        } else {
            subscriptionService.subscribe(message, arguments[0]);

            answer.setText(format("""
                    Новая подписка создана на стоимость %s USD
                    """, priceFormat(arguments[0])));

            getPriceCommand.processMessage(absSender, message, arguments);
        }

        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error occurred in /start command", e);
        }
    }

    private String priceFormat(String price) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' ');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);

        return formatter.format(Integer.parseInt(price));
    }

}