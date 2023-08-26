package com.example.kioskosnacks.RecyclerViewUtils.Models;

import java.io.Serializable;

public class ProducsCardShop implements Serializable {

    private String id;
    private String name;
    private String description;
    private int count;
    private double amount;
    private String img;
    private boolean delete;

    public ProducsCardShop(String id,String name, String description, int count, double amount, String img) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.count = count;
        this.amount=amount;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCount() {
        return count;
    }

    public double getAmount() {
        return amount;
    }

    public String getImg() {
        return img;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    @Override
    public String toString() {
        return "ProducsCardShop{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", count=" + count +
                ", amount=" + amount +
                ", img='" + img + '\'' +
                ", delete=" + delete +
                '}';
    }
}
