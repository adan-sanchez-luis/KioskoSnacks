package com.example.kioskosnacks.Marshall;

public class CashlessResponse {
    private String message;
    private MessageType type;

    public CashlessResponse(String message, MessageType type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public MessageType getType() {
        return type;
    }

    // Enum para representar los tipos de mensaje
    public enum MessageType {
        VendApproved,
        VendDenied,
        DEFAULT
    }
}
