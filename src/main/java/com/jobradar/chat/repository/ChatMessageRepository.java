package com.jobradar.chat.repository;

import com.jobradar.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderIdAndRecipientIdOrSenderIdAndRecipientId(
            String s1, String r1, String s2, String r2
    );
}

// Separate file for Message Service
/*
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

    public List<ChatMessage> getChatHistory(String s1, String r1) {
        return chatMessageRepository.findBySenderIdAndRecipientIdOrSenderIdAndRecipientId(s1, r1, r1, s1);
    }
}
*/
