package com.edupedu.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edupedu.app.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
            SELECT m FROM Message m
            JOIN FETCH m.sender
            JOIN FETCH m.recipient
            WHERE m.sender.id = :userId OR m.recipient.id = :userId
            ORDER BY m.createdAt DESC, m.id DESC
            """)
    List<Message> findAllForUser(@Param("userId") Long userId);

    @Query("""
            SELECT m FROM Message m
            JOIN FETCH m.sender
            JOIN FETCH m.recipient
            WHERE (m.sender.id = :firstUserId AND m.recipient.id = :secondUserId)
               OR (m.sender.id = :secondUserId AND m.recipient.id = :firstUserId)
            ORDER BY m.createdAt ASC, m.id ASC
            """)
    List<Message> findConversation(@Param("firstUserId") Long firstUserId, @Param("secondUserId") Long secondUserId);
}
