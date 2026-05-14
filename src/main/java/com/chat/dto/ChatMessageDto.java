package com.chat.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {

    public enum MessageType {
        ENTER, TALK, LEAVE
    }

    private Long id;
    private MessageType type;
    private Long roomId;
    private String sender;
    private String content;
    private Boolean isRead;
}