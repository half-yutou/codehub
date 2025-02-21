package cn.xyt.codehub.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatRequest {
    private String model = "deepseek-chat";
    private List<Message> messages;

    @Data
    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}