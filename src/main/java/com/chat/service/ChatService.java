package com.chat.service;

import com.chat.dto.ChatMessageDto;
import com.chat.entity.ChatRoom;
import com.chat.entity.Message;
import com.chat.repository.ChatRoomRepository;
import com.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final RedisTemplate<String, ChatMessageDto> redisTemplate;

    private static final String CHAT_KEY = "chat:room:";
    private static final long CACHE_EXPIRE = 60;

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

    // 메시지 저장 + Redis 캐싱
    public void saveMessage(ChatMessageDto dto) {
        ChatRoom room = getRoom(dto.getRoomId());
        Message message = Message.builder()
                .sender(dto.getSender())
                .content(dto.getContent())
                .chatRoom(room)
                .isRead(false)
                .build();
        messageRepository.save(message);

        // Redis에 최근 메시지 캐싱
        String key = CHAT_KEY + dto.getRoomId();
        redisTemplate.opsForList().rightPush(key, dto);
        redisTemplate.expire(key, CACHE_EXPIRE, TimeUnit.MINUTES);
    }

    // 메시지 읽음 처리
    @Transactional
    public void markAsRead(Long roomId, String sender) {
        List<Message> unreadMessages = messageRepository
                .findByChatRoomIdAndIsReadFalseAndSenderNot(roomId, sender);
        unreadMessages.forEach(m -> m.setRead(true));

        // Redis 캐시 갱신
        String key = CHAT_KEY + roomId;
        redisTemplate.delete(key);
    }

    // 안읽은 메시지 수
    public long getUnreadCount(Long roomId, String sender) {
        return messageRepository.countByChatRoomIdAndIsReadFalseAndSenderNot(roomId, sender);
    }

    // 메시지 목록 (Redis 캐시 우선, 없으면 DB)
    public List<ChatMessageDto> getMessages(Long roomId) {
        String key = CHAT_KEY + roomId;
        List<ChatMessageDto> cached = redisTemplate.opsForList().range(key, 0, -1);

        if (cached != null && !cached.isEmpty()) {
            return cached;
        }

        List<Message> messages = messageRepository.findByChatRoomIdOrderBySentAtAsc(roomId);
        List<ChatMessageDto> dtos = messages.stream()
                .map(m -> ChatMessageDto.builder()
                        .type(ChatMessageDto.MessageType.TALK)
                        .roomId(roomId)
                        .sender(m.getSender())
                        .content(m.getContent())
                        .isRead(m.isRead())
                        .build())
                .toList();

        dtos.forEach(dto -> redisTemplate.opsForList().rightPush(key, dto));
        if (!dtos.isEmpty()) {
            redisTemplate.expire(key, CACHE_EXPIRE, TimeUnit.MINUTES);
        }

        return dtos;
    }
    
    // 메시지 삭제
    @Transactional
    public void deleteMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    // 채팅방 삭제
    @Transactional
    public void deleteRoom(Long roomId) {
        // 채팅방 메시지 먼저 삭제
        messageRepository.deleteByChatRoomId(roomId);
        // Redis 캐시 삭제
        redisTemplate.delete(CHAT_KEY + roomId);
        // 채팅방 삭제
        chatRoomRepository.deleteById(roomId);
    }
}