package com.example.sungyoung.testproject1.account;

public class Account {
    String date;
    String accountName;
    String price;
    String imex;
    String id;

    public Account(String date, String accountName, String price, String imex) {
        this.date = date;
        this.accountName = accountName;
        this.price = price;
        this.imex = imex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImex() {
        return imex;
    }

    public void setImex(String imex) {
        this.imex = imex;
    }
}

