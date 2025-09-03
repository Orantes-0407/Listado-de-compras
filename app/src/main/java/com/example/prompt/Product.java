package com.example.prompt;

public class Product {
    private int id;
    private String name;
    private String status;
    private String date;

    public Product(int id, String name, String status, String date) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }
}

