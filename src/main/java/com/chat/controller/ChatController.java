package com.chat.controller;

import com.chat.dto.ChatMessageDto;
import com.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(ChatMessageDto dto) {
        if (ChatMessageDto.MessageType.ENTER.equals(dto.getType())) {
            dto.setContent(dto.getSender() + "님이 입장했습니다.");
        } else if (ChatMessageDto.MessageType.LEAVE.equals(dto.getType())) {
            dto.setContent(dto.getSender() + "님이 퇴장했습니다.");
        } else {
            chatService.saveMessage(dto);
        }
        messagingTemplate.convertAndSend("/sub/chat/room/" + dto.getRoomId(), dto);
    }
}