package com.example.kioskosnacks.WebService.Models;

import java.io.Serializable;

public class ConceptReport implements Serializable {
    private String id;
    private String name;
    private String description;
    private String unit;
    private String price;
    private String acountType;
    private String tag;
    private String group;
    private String notes;
    private int status;
    private int quantity;
    private int totalSales;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAcountType() {
        return acountType;
    }

    public void setAcountType(String acountType) {
        this.acountType = acountType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    @Override
    public String toString() {
        return "Concept{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", unit=" + unit +
                ", price=" + price +
                ", acountType=" + acountType +
                ", tag=" + tag +
                ", group=" + group +
                ", notes='" + notes + '\'' +
                ", status=" + status +
                ", quantity=" + quantity +
                '}';
    }
}
