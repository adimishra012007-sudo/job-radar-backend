package com.jobradar.chat.service;

import com.jobradar.chat.entity.ChatMessage;
import com.jobradar.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setRead(false);
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getChatHistory(String senderId, String recipientId) {
        return chatMessageRepository.findBySenderIdAndRecipientIdOrSenderIdAndRecipientId(
                senderId, recipientId, recipientId, senderId
        );
    }
}
