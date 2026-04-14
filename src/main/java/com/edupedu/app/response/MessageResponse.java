package com.edupedu.app.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageResponse(
        Long id,

        @JsonProperty("sender_id")
        Long senderId,

        @JsonProperty("sender_name")
        String senderName,

        @JsonProperty("recipient_id")
        Long recipientId,

        @JsonProperty("recipient_name")
        String recipientName,

        String content,

        @JsonProperty("sent_by_me")
        boolean sentByMe,

        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
}
