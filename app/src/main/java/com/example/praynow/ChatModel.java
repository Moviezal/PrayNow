package com.example.praynow;

public class ChatModel {
    public static String SENT_BY_ME = "me";
    public static String SENT_BY_BOT = "bot";

    private String message;
    private String sentBy;

    public ChatModel(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }

    public String getMessage() { return message; }
    public String getSentBy() { return sentBy; }
}