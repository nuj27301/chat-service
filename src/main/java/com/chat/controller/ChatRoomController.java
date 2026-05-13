package com.chat.controller;

import com.chat.entity.ChatRoom;
import com.chat.entity.Message;
import com.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatService chatService;

    // 채팅방 목록 페이지
    @GetMapping("/")
    public String rooms(Model model) {
        List<ChatRoom> rooms = chatService.getRooms();
        model.addAttribute("rooms", rooms);
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
    public String room(@PathVariable Long roomId, Model model) {
        ChatRoom room = chatService.getRoom(roomId);
        List<Message> messages = chatService.getMessages(roomId);
        model.addAttribute("room", room);
        model.addAttribute("messages", messages);
        return "room";
    }
}