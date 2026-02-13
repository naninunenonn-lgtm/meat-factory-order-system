package com.meatfactory.order.model;

import java.time.LocalDate;
import java.util.List;

/**
 * 注文詳細画面に必要なデータをまとめた入れ物。
 * - ヘッダ情報（注文ID/日付/取引先/ステータス）
 * - 明細一覧（肉/数量/単価/金額）
 */
public class OrderDetail {

    private int orderId;
    private LocalDate orderDate;
    private String customerName;
    private String status;
    private int totalAmount;

    private List<OrderDetailItem> items;

    public OrderDetail(int orderId, LocalDate orderDate, String customerName, String status, int totalAmount,
                       List<OrderDetailItem> items) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.customerName = customerName;
        this.status = status;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public int getOrderId() { return orderId; }
    public LocalDate getOrderDate() { return orderDate; }
    public String getCustomerName() { return customerName; }
    public String getStatus() { return status; }
    public int getTotalAmount() { return totalAmount; }
    public List<OrderDetailItem> getItems() { return items; }
}
