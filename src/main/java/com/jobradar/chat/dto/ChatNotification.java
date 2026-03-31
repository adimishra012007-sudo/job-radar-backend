package com.jobradar.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatNotification {
    private String senderId;
    private String recipientId;
    private NotificationType type;

    public enum NotificationType {
        TYPING, READ_RECEIPT
    }
}
