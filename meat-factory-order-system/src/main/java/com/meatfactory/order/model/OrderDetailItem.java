package com.meatfactory.order.model;

/**
 * 注文詳細の明細1行分。
 */
public class OrderDetailItem {

    private String meatName;
    private int quantity;
    private int unitPrice;
    private int amount;

    public OrderDetailItem(String meatName, int quantity, int unitPrice, int amount) {
        this.meatName = meatName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
    }

    public String getMeatName() { return meatName; }
    public int getQuantity() { return quantity; }
    public int getUnitPrice() { return unitPrice; }
    public int getAmount() { return amount; }
}
