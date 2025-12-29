package com.meatfactory.order.model;

public class OrderItem {

    private String meatCode;  // ★ BEEF
    private String meatName;  // ★ 牛肉（表示用）
    private String part;
    private int quantity;
    private int price;

    public OrderItem(String meatCode, String meatName, String part, int quantity, int price) {
        this.meatCode = meatCode;
        this.meatName = meatName;
        this.part = part;
        this.quantity = quantity;
        this.price = price;
    }

    public String getMeatCode() {
        return meatCode;
    }

    public String getMeatName() {
        return meatName;
    }

    public String getPart() {
        return part;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public int getSubtotal() {
        return quantity * price;
    }
}
