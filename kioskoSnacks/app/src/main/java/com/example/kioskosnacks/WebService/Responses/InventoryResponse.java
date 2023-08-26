package com.example.kioskosnacks.WebService.Responses;

import com.example.kioskosnacks.WebService.Models.Concept;

public class InventoryResponse {
    private String message;
    private MessageType type;
    private Concept[] concepts;

    public InventoryResponse(String message, MessageType type, Concept[] concepts) {
        this.message = message;
        this.type = type;
        this.concepts = concepts;
    }

    public String getMessage() {
        return message;
    }

    public MessageType getType() {
        return type;
    }

    public Concept[] getConcepts() {
        return concepts;
    }

    // Enum para representar los tipos de mensaje
    public enum MessageType {
        SUCCESS,
        ERROR,
        NULL_JSON,
        REQUEST_ERROR
    }
}
