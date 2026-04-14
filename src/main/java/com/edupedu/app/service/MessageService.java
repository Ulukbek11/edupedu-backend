package com.edupedu.app.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edupedu.app.exception.ResourceNotFoundException;
import com.edupedu.app.model.Message;
import com.edupedu.app.model.User;
import com.edupedu.app.repository.MessageRepository;
import com.edupedu.app.repository.UserRepository;
import com.edupedu.app.request.SendMessageRequest;
import com.edupedu.app.response.ConversationSummaryResponse;
import com.edupedu.app.response.MessageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional
    public MessageResponse sendMessage(User authenticatedUser, SendMessageRequest request) {
        return createMessage(authenticatedUser, request).message();
    }

    @Transactional
    public MessageDelivery sendMessageForRealtime(User authenticatedUser, SendMessageRequest request) {
        return createMessage(authenticatedUser, request);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMyMessages(User authenticatedUser) {
        Long myUserId = resolveAuthenticatedUserId(authenticatedUser);
        return messageRepository.findAllForUser(myUserId)
                .stream()
                .map(message -> mapToMessageResponse(message, myUserId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getConversation(User authenticatedUser, Long otherUserId) {
        Long myUserId = resolveAuthenticatedUserId(authenticatedUser);
        if (myUserId.equals(otherUserId)) {
            throw new IllegalArgumentException("Use another user id to fetch a conversation");
        }

        if (!userRepository.existsById(otherUserId)) {
            throw new ResourceNotFoundException("User", "id", otherUserId);
        }

        return messageRepository.findConversation(myUserId, otherUserId)
                .stream()
                .map(message -> mapToMessageResponse(message, myUserId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConversationSummaryResponse> getMyConversations(User authenticatedUser) {
        Long myUserId = resolveAuthenticatedUserId(authenticatedUser);
        List<Message> allMessages = messageRepository.findAllForUser(myUserId);

        Map<Long, ConversationSummaryResponse> conversations = new LinkedHashMap<>();
        for (Message message : allMessages) {
            boolean sentByMe = message.getSender().getId().equals(myUserId);
            User otherUser = sentByMe ? message.getRecipient() : message.getSender();
            if (!conversations.containsKey(otherUser.getId())) {
                conversations.put(otherUser.getId(), mapToConversationSummary(message, myUserId));
            }
        }

        return new ArrayList<>(conversations.values());
    }

    public record MessageDelivery(MessageResponse message, String senderUsername, String recipientUsername) {
    }

    private MessageDelivery createMessage(User authenticatedUser, SendMessageRequest request) {
        Long senderId = resolveAuthenticatedUserId(authenticatedUser);

        if (senderId.equals(request.recipientId())) {
            throw new IllegalArgumentException("You cannot send a message to yourself");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", senderId));
        User recipient = userRepository.findById(request.recipientId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.recipientId()));

        String content = request.content().trim();
        if (content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be blank");
        }

        Message message = Message.builder()
                .sender(sender)
                .recipient(recipient)
                .content(content)
                .build();

        Message saved = messageRepository.save(message);
        return new MessageDelivery(mapToMessageResponse(saved, senderId), sender.getUsername(), recipient.getUsername());
    }

    private Long resolveAuthenticatedUserId(User authenticatedUser) {
        if (authenticatedUser == null || authenticatedUser.getId() == null) {
            throw new IllegalStateException("Authenticated user is required");
        }
        return authenticatedUser.getId();
    }

    private MessageResponse mapToMessageResponse(Message message, Long viewerId) {
        boolean sentByMe = message.getSender().getId().equals(viewerId);
        return new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getFullName(),
                message.getRecipient().getId(),
                message.getRecipient().getFullName(),
                message.getContent(),
                sentByMe,
                message.getCreatedAt()
        );
    }

    private ConversationSummaryResponse mapToConversationSummary(Message message, Long viewerId) {
        boolean sentByMe = message.getSender().getId().equals(viewerId);
        User otherUser = sentByMe ? message.getRecipient() : message.getSender();

        return new ConversationSummaryResponse(
                otherUser.getId(),
                otherUser.getFullName(),
                message.getContent(),
                sentByMe,
                message.getCreatedAt()
        );
    }
}
