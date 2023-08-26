package com.example.kioskosnacks.RecyclerViewUtils.Models;

import java.io.Serializable;

public class ProducsInventory implements Serializable {
    private String name;
    private String description;
    private String img;
    private String code;
    private int stock;

    public ProducsInventory(String name, String description, String img, String code, int stock) {
        this.name = name;
        this.description = description;
        this.img = img;
        this.code = code;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImg() {
        return img;
    }

    public String getCode() {
        return code;
    }

    public int getStock() {
        return stock;
    }

    @Override
    public String toString() {
        return "ProducsInventory{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", img='" + img + '\'' +
                ", code='" + code + '\'' +
                ", stock='" + stock + '\'' +
                '}';
    }
}
