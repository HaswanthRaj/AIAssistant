package com.haswanth.aiassistant;

public class Message {
    public String text;
    public boolean isUser;
    public String timestamp; // Time of message

    public Message(String text, boolean isUser, String timestamp) {
        this.text = text;
        this.isUser = isUser;
        this.timestamp = timestamp;
    }
}

