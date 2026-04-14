package com.edupedu.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edupedu.app.model.User;
import com.edupedu.app.request.SendMessageRequest;
import com.edupedu.app.response.ConversationSummaryResponse;
import com.edupedu.app.response.MessageResponse;
import com.edupedu.app.service.MessageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SendMessageRequest request) {
        return new ResponseEntity<>(messageService.sendMessage(user, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MessageResponse>> getMyMessages(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getMyMessages(user));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationSummaryResponse>> getMyConversations(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getMyConversations(user));
    }

    @GetMapping("/conversations/{otherUserId}")
    public ResponseEntity<List<MessageResponse>> getConversation(
            @AuthenticationPrincipal User user,
            @PathVariable Long otherUserId) {
        return ResponseEntity.ok(messageService.getConversation(user, otherUserId));
    }
}
