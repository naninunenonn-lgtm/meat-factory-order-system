/*
 * =====================================================================
 * OrderDetail
 * =====================================================================
 * 【ファイルの場所】
 * model/OrderDetail.java
 *
 * 【主な種類】
 * モデルクラス
 *
 * 【このファイルの大きな目的】
 * 画面・Servlet・DAOの間で受け渡すデータ入れ物。
*/

package com.meatfactory.order.model;

import java.time.LocalDate;
import java.util.List;

/**
 * 注文詳細画面に必要なデータをまとめる。
 * - ヘッダ情報（注文ID/日付/取引先/ステータス）
 * - 明細一覧（肉/数量/単価/金額）
 * - class：OrderDetail
 */

public class OrderDetail {

//	 フィールド
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
    
    /**
     * JSP表示用：ステータスを日本語に変換して返す
     * JSPから ${detail.statusLabel} と書くと呼ばれる
     */
    public String getStatusLabel() {
        return OrderStatus.label(status);
    }

}
