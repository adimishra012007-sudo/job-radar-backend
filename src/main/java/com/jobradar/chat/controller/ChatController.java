package com.jobradar.chat.controller;

import com.jobradar.chat.entity.ChatMessage;
import com.jobradar.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        ChatMessage savedMsg = chatService.save(chatMessage);
        String recipientId = chatMessage.getRecipientId();
        if (recipientId != null) {
            messagingTemplate.convertAndSendToUser(
                    recipientId, "/queue/messages",
                    savedMsg
            );
        }
    }

    @MessageMapping("/chat/typing")
    public void handleTyping(@Payload com.jobradar.chat.dto.ChatNotification notification) {
        String recipientId = notification.getRecipientId();
        if (recipientId != null) {
            messagingTemplate.convertAndSendToUser(
                recipientId, "/queue/typing",
                notification
            );
        }
    }

    @MessageMapping("/chat/read")
    public void handleReadReceipt(@Payload com.jobradar.chat.dto.ChatNotification notification) {
        // Business logic for marking as read can be added here
        String recipientId = notification.getRecipientId();
        if (recipientId != null) {
            messagingTemplate.convertAndSendToUser(
                recipientId, "/queue/read",
                notification
            );
        }
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<com.jobradar.common.dto.ApiResponse<List<ChatMessage>>> findChatMessages(
            @PathVariable String senderId,
            @PathVariable String recipientId
    ) {
        return ResponseEntity.ok(com.jobradar.common.dto.ApiResponse.success(
            "Success: Comms history decrypted.", 
            chatService.getChatHistory(senderId, recipientId)
        ));
    }
}
