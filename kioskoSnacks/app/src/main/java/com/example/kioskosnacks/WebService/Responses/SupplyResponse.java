package com.example.kioskosnacks.WebService.Responses;

public class SupplyResponse {
    private String message;
    private MessageType type;
    private boolean status;

    public SupplyResponse(String message, MessageType type, boolean status) {
        this.message = message;
        this.type = type;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public MessageType getType() {
        return type;
    }

    public boolean getStatus() {
        return status;
    }

    // Enum para representar los tipos de mensaje
    public enum MessageType {
        SUCCESS,
        NOTFOUND,
        ERROR,
        NULL_JSON,
        REQUEST_ERROR
    }
}
