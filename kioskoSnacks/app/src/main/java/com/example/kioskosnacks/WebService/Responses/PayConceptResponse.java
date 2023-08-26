package com.example.kioskosnacks.WebService.Responses;

public class PayConceptResponse {
    private String message;
    private MessageType type;
    private boolean save;

    public PayConceptResponse(String message, MessageType type, boolean save) {
        this.message = message;
        this.type = type;
        this.save = save;
    }

    public String getMessage() {
        return message;
    }

    public MessageType getType() {
        return type;
    }

    public boolean getSave() {
        return save;
    }

    // Enum para representar los tipos de mensaje
    public enum MessageType {
        SUCCESS,
        ERROR,
        NULL_JSON,
        REQUEST_ERROR
    }
}
