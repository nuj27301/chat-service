package com.chat.service;

import com.chat.dto.ChatMessageDto;
import com.chat.entity.ChatRoom;
import com.chat.entity.Message;
import com.chat.repository.ChatRoomRepository;
import com.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    // 채팅방 생성
    public ChatRoom createRoom(String name) {
        ChatRoom room = ChatRoom.builder()
                .name(name)
                .build();
        return chatRoomRepository.save(room);
    }

    // 채팅방 목록
    public List<ChatRoom> getRooms() {
        return chatRoomRepository.findAll();
    }

    // 채팅방 단건 조회
    public ChatRoom getRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방 없음"));
    }

    // 메시지 저장
    public void saveMessage(ChatMessageDto dto) {
        ChatRoom room = getRoom(dto.getRoomId());
        Message message = Message.builder()
                .sender(dto.getSender())
                .content(dto.getContent())
                .chatRoom(room)
                .build();
        messageRepository.save(message);
    }

    // 메시지 목록
    public List<Message> getMessages(Long roomId) {
        return messageRepository.findByChatRoomIdOrderBySentAtAsc(roomId);
    }
}