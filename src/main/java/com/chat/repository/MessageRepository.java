package com.chat.repository;

import com.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomIdOrderBySentAtAsc(Long roomId);
    List<Message> findByChatRoomIdAndIsReadFalseAndSenderNot(Long roomId, String sender);
    long countByChatRoomIdAndIsReadFalseAndSenderNot(Long roomId, String sender);
    void deleteByChatRoomId(Long roomId);
}