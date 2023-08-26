package com.example.kioskosnacks.WebService.Responses;

public class LoginResponse {
    private String message;
    private MessageType type;
    private String token;

    public LoginResponse(String message, MessageType type, String token) {
        this.message = message;
        this.type = type;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public MessageType getType() {
        return type;
    }

    public String getToken() {
        return token;
    }

    // Enum para representar los tipos de mensaje
    public enum MessageType {
        SUCCESS,
        ERROR,
        NULL_JSON,
        REQUEST_ERROR
    }
}
