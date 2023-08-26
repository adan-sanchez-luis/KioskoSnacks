package com.example.kioskosnacks.WebService.Responses;

import com.example.kioskosnacks.WebService.Models.Concept;

public class ConceptResponse {
    private String message;
    private MessageType type;
    private Concept concept;

    public ConceptResponse(String message, MessageType type, Concept concept) {
        this.message = message;
        this.type = type;
        this.concept = concept;
    }

    public String getMessage() {
        return message;
    }

    public MessageType getType() {
        return type;
    }

    public Concept getConcept() {
        return concept;
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
