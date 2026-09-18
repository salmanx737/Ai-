package com.example.a55;

public class Message {

    private String message;
    private boolean isUser;
    private long timestamp;
    private boolean isTyping;

    // Normal message constructor
    public Message(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
        this.isTyping = false;
    }

    // Typing indicator constructor
    public Message(boolean isTyping) {
        this.message = "";
        this.isUser = false;
        this.timestamp = System.currentTimeMillis();
        this.isTyping = isTyping;
    }

    // Getter
    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isTyping() {
        return isTyping;
    }

    // Setter (useful for streaming responses)
    public void setMessage(String message) {
        this.message = message;
    }

}