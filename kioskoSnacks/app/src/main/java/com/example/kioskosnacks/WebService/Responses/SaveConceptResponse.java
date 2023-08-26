package com.example.kioskosnacks.WebService.Responses;

public class SaveConceptResponse {
    private String message;
    private MessageType type;
    private String concept;

    public SaveConceptResponse(String message, MessageType type, String concept) {
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

    public String getConcept() {
        return concept;
    }

    // Enum para representar los tipos de mensaje
    public enum MessageType {
        SUCCESS,
        ERROR,
        NULL_JSON,
        REQUEST_ERROR
    }
}
