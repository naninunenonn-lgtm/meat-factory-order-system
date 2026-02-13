package com.meatfactory.order.model;

import java.time.LocalDate;

/**
 * CSV出力（帳票）で「1行分」を表すDTO。
 *
 * 重要：
 * - 注文ヘッダ（orders）の情報も
 * - 明細（order_item）の情報も
 * 1行にまとめて持つ（帳票CSVでよくある形）
 *
 * 1行 = order_item 1件（明細1行）
 */
public class OrderItemCsvRow {

    // ===== 注文ヘッダ側（orders） =====
    private int orderId;            // 注文ID
    private LocalDate orderDate;    // 注文日
    private String customerCode;    // 取引先コード（C001など）
    private String customerName;    // 取引先名
    private String status;          // 注文ステータス

    // ===== 明細側（order_item + meat） =====
    private String meatCode;        // 肉コード（BEEFなど）
    private String meatName;        // 肉名称（牛肉など）
    private int quantity;           // 数量（kg）
    private int unitPrice;          // 単価（円）
    private int amount;             // 金額（数量×単価）

    /**
     * まとめて全部受け取るコンストラクタ。
     * DAOがResultSetから取り出して、このDTOに詰めて返す。
     */
    public OrderItemCsvRow(int orderId, LocalDate orderDate,
                           String customerCode, String customerName,
                           String status,
                           String meatCode, String meatName,
                           int quantity, int unitPrice, int amount) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.status = status;
        this.meatCode = meatCode;
        this.meatName = meatName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
    }

    // ===== getter（ServletがCSV出力するときに使う） =====
    public int getOrderId() { return orderId; }
    public LocalDate getOrderDate() { return orderDate; }
    public String getCustomerCode() { return customerCode; }
    public String getCustomerName() { return customerName; }
    public String getStatus() { return status; }

    public String getMeatCode() { return meatCode; }
    public String getMeatName() { return meatName; }
    public int getQuantity() { return quantity; }
    public int getUnitPrice() { return unitPrice; }
    public int getAmount() { return amount; }
}
