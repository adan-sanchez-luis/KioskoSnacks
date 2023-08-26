package com.example.kioskosnacks.WebService.Models;

import java.io.Serializable;

public class Concept implements Serializable {
    private String id;
    private String name;
    private String description;
    private int unit;
    private double price;
    private int acountType;
    private int tag;
    private int group;
    private String notes;
    private int status;
    private int quantity;
    private int inCart;
    private String img;

    private boolean delete;


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

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAcountType() {
        return acountType;
    }

    public void setAcountType(int acountType) {
        this.acountType = acountType;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
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

    public int getInCart() {
        return inCart;
    }

    public void setInCart(int inCart) {
        this.inCart = inCart;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
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
