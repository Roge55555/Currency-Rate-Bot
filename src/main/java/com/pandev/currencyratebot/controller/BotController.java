package com.pandev.currencyratebot.controller;

import com.pandev.currencyratebot.config.BotConfig;
import com.pandev.currencyratebot.entity.Message;
import com.pandev.currencyratebot.service.CurrencyService;
import com.pandev.currencyratebot.service.MessageService;
import com.pandev.currencyratebot.service.SpeechToTextService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Component
@AllArgsConstructor
public class BotController extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    private final CurrencyService currencyService;

    private final SpeechToTextService speechToTextService;

    private final MessageService messageService;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String reply = "";

        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if ("/start".equals(messageText)) {
                startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
            } else if ("/convert".equals(messageText)) {

                try {
                    File tempFile = File.createTempFile("file", ".wav");

                    byte[] content = null;
                    try {
                        content = Files.readAllBytes(Path.of("src/main/resources/fulltest.wav"));
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }

                    MultipartFile file = new MockMultipartFile("example.wav", "example.wav", "audio/wave", content);
                    file.transferTo(tempFile);
                    String text = speechToTextService.convertAudioToText(tempFile);
                    tempFile.delete();

                    reply = text;
                } catch (IOException e) {
                    reply = "Error converting audio to text";
                }

            } else {
                reply = ((messageText.contains("$") && messageText.length() > 1) ||
                        (messageText.contains("тенге") && messageText.length() > 5)) ?
                        currencyService.currencyExchange(messageText.replace(" ", "")) : "Please enter value in $ or тенге.";
            }

            messageService.add(Message.builder().messageId(update.getMessage().getChatId())
                    .userId(Long.valueOf(update.getMessage().getMessageId()))
                    .message(reply.replace(" ", "")).build());

            sendMessage(chatId, reply);
        }

    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!" + "\n" +
                "Enter the currency whose official exchange rate" + "\n" +
                "you want to know in relation to BYN." + "\n" +
                "For example: USD";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
