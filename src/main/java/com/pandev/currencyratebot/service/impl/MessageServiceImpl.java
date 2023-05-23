package com.pandev.currencyratebot.service.impl;

import com.pandev.currencyratebot.entity.Message;
import com.pandev.currencyratebot.repository.MessageRepository;
import com.pandev.currencyratebot.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public Message add(Message message) {
        return messageRepository.save(message);
    }
}
