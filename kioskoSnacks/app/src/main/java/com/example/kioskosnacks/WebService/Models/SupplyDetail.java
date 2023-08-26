package com.example.kioskosnacks.WebService.Models;

public class SupplyDetail {
    private int status;
    private String idConcept;
    private int quantity;

    public SupplyDetail(int status, String idConcept, int quantity) {
        this.status = status;
        this.idConcept = idConcept;
        this.quantity = quantity;
    }
}
