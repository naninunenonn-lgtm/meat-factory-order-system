package com.meatfactory.order.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.meatfactory.order.model.OrderDetail;
import com.meatfactory.order.model.OrderDetailItem;

/**
 * 注文詳細（1件）をDBから取得するDAO。
 * URLの id（order_id）をキーに、ヘッダ＋明細を取得する。
 */
public class OrderDetailDao {

    /**
     * order_id を指定して注文詳細を取得する。
     * 該当がなければ null を返す。
     */
    public OrderDetail findById(int orderId) throws Exception {

        // ① ヘッダ（orders + customer + 合計）
        String headerSql = """
            SELECT
              o.order_id,
              o.order_date,
              o.status,
              c.name AS customer_name,
              COALESCE(SUM(oi.amount), 0) AS total_amount
            FROM orders o
            LEFT JOIN customer c ON c.customer_id = o.customer_id
            LEFT JOIN order_item oi ON oi.order_id = o.order_id
            WHERE o.order_id = ?
            GROUP BY o.order_id, o.order_date, o.status, c.name
        """;

        // ② 明細（order_item + meat）
        String itemSql = """
            SELECT
              m.name AS meat_name,
              oi.quantity,
              oi.unit_price,
              oi.amount
            FROM order_item oi
            JOIN meat m ON m.meat_id = oi.meat_id
            WHERE oi.order_id = ?
            ORDER BY oi.item_id
        """;

        try (Connection con = DBUtil.getConnection()) {

            // --- ヘッダ取得 ---
            LocalDate orderDate;
            String status;
            String customerName;
            int totalAmount;

            try (PreparedStatement ps = con.prepareStatement(headerSql)) {
                ps.setInt(1, orderId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null; // 注文が存在しない
                    }
                    orderDate = rs.getDate("order_date").toLocalDate();
                    status = rs.getString("status");
                    customerName = rs.getString("customer_name"); // nullの場合あり（未設定）
                    totalAmount = rs.getInt("total_amount");
                }
            }

            // --- 明細取得 ---
            List<OrderDetailItem> items = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(itemSql)) {
                ps.setInt(1, orderId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        items.add(new OrderDetailItem(
                                rs.getString("meat_name"),
                                rs.getInt("quantity"),
                                rs.getInt("unit_price"),
                                rs.getInt("amount")
                        ));
                    }
                }
            }

            // DTOにまとめて返す
            return new OrderDetail(orderId, orderDate, customerName, status, totalAmount, items);
        }
    }
}
