package com.example.kioskosnacks.WebService.Models;

public class SalesDetails {
    private String idConcept;
    private int quantity;
    private double price;
    private int status;

    public SalesDetails(String idConcept, int quantity, double price, int status) {
        this.idConcept = idConcept;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
    }
}
