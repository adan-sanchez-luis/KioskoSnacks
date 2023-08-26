package com.example.kioskosnacks.WebService.Responses;

import com.example.kioskosnacks.WebService.Models.ConceptReport;

public class ReportResponse {
    private String message;
    private MessageType type;
    private ConceptReport[] topSales;

    public ReportResponse(String message, MessageType type, ConceptReport[] topSales) {
        this.message = message;
        this.type = type;
        this.topSales = topSales;
    }

    public String getMessage() {
        return message;
    }

    public MessageType getType() {
        return type;
    }

    public ConceptReport[] getTopSales() {
        return topSales;
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
