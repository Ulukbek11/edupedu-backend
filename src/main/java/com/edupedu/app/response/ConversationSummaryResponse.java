package com.edupedu.app.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConversationSummaryResponse(
        @JsonProperty("other_user_id")
        Long otherUserId,

        @JsonProperty("other_user_name")
        String otherUserName,

        @JsonProperty("last_message")
        String lastMessage,

        @JsonProperty("last_message_sent_by_me")
        boolean lastMessageSentByMe,

        @JsonProperty("last_message_at")
        LocalDateTime lastMessageAt
) {
}
