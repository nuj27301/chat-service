package com.chat.controller;

import com.chat.dto.ChatMessageDto;
import com.chat.entity.ChatRoom;
import com.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatService chatService;

    // 채팅방 목록 페이지
    @GetMapping("/")
    public String rooms(Model model,
                        @RequestParam(required = false, defaultValue = "익명") String username) {
        List<ChatRoom> rooms = chatService.getRooms();
        Map<Long, Long> unreadCounts = new HashMap<>();
        rooms.forEach(room ->
            unreadCounts.put(room.getId(), chatService.getUnreadCount(room.getId(), username))
        );
        model.addAttribute("rooms", rooms);
        model.addAttribute("unreadCounts", unreadCounts);
        model.addAttribute("username", username);
        return "rooms";
    }

    // 채팅방 생성
    @PostMapping("/room")
    public String createRoom(@RequestParam String name) {
        chatService.createRoom(name);
        return "redirect:/";
    }

    // 채팅방 입장
    @GetMapping("/room/{roomId}")
    public String room(@PathVariable Long roomId, 
                       @RequestParam(required = false, defaultValue = "익명") String username,
                       Model model) {
        ChatRoom room = chatService.getRoom(roomId);
        List<ChatMessageDto> messages = chatService.getMessages(roomId);
        model.addAttribute("room", room);
        model.addAttribute("messages", messages);
        model.addAttribute("username", username);
        return "room";
    }
    
    // 채팅방 삭제
    @PostMapping("/room/{roomId}/delete")
    public String deleteRoom(@PathVariable Long roomId) {
        chatService.deleteRoom(roomId);
        return "redirect:/";
    }

    // 메시지 삭제
    @PostMapping("/room/{roomId}/message/{messageId}/delete")
    public String deleteMessage(@PathVariable Long roomId, @PathVariable Long messageId) {
        chatService.deleteMessage(messageId);
        return "redirect:/room/" + roomId;
    }
}