package com.meatfactory.order.model;

import java.time.LocalDate;

/**
 * 注文一覧画面に表示する「1行分のデータ」をまとめたクラス（DTO）。
 *
 * DBには
 *   orders        : 注文ヘッダ
 *   customer      : 取引先
 *   order_item    : 明細（複数行）
 * が分かれて保存されているが、
 *
 * 一覧画面では
 *   「注文ID・日付・取引先名・ステータス・合計金額」
 * を1行で扱いたい。
 *
 * そのため DAO で集計した結果をこのクラスに詰めて JSP に渡す。
 *
 * JSPでは ${o.orderId} のように getter が自動で呼ばれて表示される。
 */
public class OrderSummary {

    /** 注文番号（orders.order_id） */
    private int orderId;

    /** 注文日（orders.order_date） */
    private LocalDate orderDate;

    /** 取引先名（customer.name） */
    private String customerName;

    /** ステータス（orders.status） */
    private String status;

    /** 注文の合計金額（SUM(order_item.amount)） */
    private int totalAmount;

    /**
     * 一覧1行分のデータをまとめて受け取るコンストラクタ。
     * DAOで取得したDBの値をそのまま格納する用途。
     */
    public OrderSummary(int orderId, LocalDate orderDate, String customerName, String status, int totalAmount) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.customerName = customerName;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    // ===== JSP表示用 getter =====
    public int getOrderId() { return orderId; }
    public LocalDate getOrderDate() { return orderDate; }
    public String getCustomerName() { return customerName; }
    public String getStatus() { return status; }
    public int getTotalAmount() { return totalAmount; }
}
