package com.edupedu.app.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.edupedu.app.model.User;
import com.edupedu.app.request.SendMessageRequest;
import com.edupedu.app.service.MessageService;
import com.edupedu.app.service.MessageService.MessageDelivery;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Valid SendMessageRequest request, Principal principal) {
        User sender = resolveAuthenticatedUser(principal);
        MessageDelivery delivery = messageService.sendMessageForRealtime(sender, request);

        messagingTemplate.convertAndSendToUser(delivery.recipientUsername(), "/queue/messages", delivery.message());
        messagingTemplate.convertAndSendToUser(delivery.senderUsername(), "/queue/messages", delivery.message());
    }

    private User resolveAuthenticatedUser(Principal principal) {
        if (principal instanceof Authentication authentication
                && authentication.getPrincipal() instanceof User user) {
            return user;
        }
        throw new IllegalStateException("Authenticated websocket user is required");
    }
}
