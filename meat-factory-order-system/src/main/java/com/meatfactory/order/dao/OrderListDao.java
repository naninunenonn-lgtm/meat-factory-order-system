package com.meatfactory.order.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.meatfactory.order.model.OrderSummary;

/**
 * 注文一覧画面に表示するデータをDBから取得するDAO。
 *
 * 役割：
 *   DB → Javaオブジェクトへ変換する
 *
 * サーブレットは「画面制御」だけ、
 * DB操作はDAOに分離するのがMVCの基本設計。
 */
public class OrderListDao {

    /**
     * 注文一覧を全件取得する。
     *
     * DB構造は
     *   orders      : 注文ヘッダ（1件）
     *   order_item  : 明細（複数行）
     *
     * 一覧では「合計金額」を出したいので
     * order_item.amount を SUM で集計する。
     */
    public List<OrderSummary> findAll() throws Exception {

        // 注文ヘッダ + 取引先 + 明細合計金額 をまとめて取得するSQL
        String sql = """
            SELECT
              o.order_id,
              o.order_date,
              o.status,
              COALESCE(c.name, '(未設定)') AS customer_name,
              COALESCE(SUM(oi.amount), 0) AS total_amount
            FROM orders o
            LEFT JOIN customer c ON c.customer_id = o.customer_id
            LEFT JOIN order_item oi ON oi.order_id = o.order_id
            GROUP BY o.order_id, o.order_date, COALESCE(c.name, '(未設定)'), o.status
            ORDER BY o.order_id DESC
        """;

        List<OrderSummary> list = new ArrayList<>();

        // try-with-resources：DB接続・Statement・ResultSetを自動でcloseする
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // 1レコードずつ読み取り、DTOへ詰め替える
            while (rs.next()) {

                OrderSummary s = new OrderSummary(
                        rs.getInt("order_id"),
                        rs.getDate("order_date").toLocalDate(),
                        rs.getString("customer_name"),
                        rs.getString("status"),
                        rs.getInt("total_amount")
                );

                // 一覧表示用のリストに追加
                list.add(s);
            }
        }

        // サーブレットへ返す（＝画面に渡るデータ）
        return list;
    }
    
    public List<OrderSummary> findByCriteria(Long customerId, java.time.LocalDate from, java.time.LocalDate to) throws Exception {

        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT
              o.order_id,
              o.order_date,
              COALESCE(c.name, '(未設定)') AS customer_name,
              o.status,
              COALESCE(SUM(oi.amount), 0) AS total_amount
            FROM orders o
            LEFT JOIN customer c ON c.customer_id = o.customer_id
            LEFT JOIN order_item oi ON oi.order_id = o.order_id
            WHERE 1=1
        """);

        List<Object> params = new java.util.ArrayList<>();

        // ① 取引先
        if (customerId != null) {
            sql.append(" AND o.customer_id = ? ");
            params.add(customerId);
        }

        // ② 開始日
        if (from != null) {
            sql.append(" AND o.order_date >= ? ");
            params.add(java.sql.Date.valueOf(from));
        }

        // ③ 終了日
        if (to != null) {
            sql.append(" AND o.order_date <= ? ");
            params.add(java.sql.Date.valueOf(to));
        }

        sql.append("""
            GROUP BY o.order_id, o.order_date, c.name, o.status
            ORDER BY o.order_id DESC
        """);

        try (java.sql.Connection con = DBUtil.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql.toString())) {

            // パラメータを順番にセット
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                List<OrderSummary> list = new java.util.ArrayList<>();
                while (rs.next()) {
                    // 既存の OrderSummary のコンストラクタに合わせて詰め替え
                    list.add(new OrderSummary(
                        rs.getInt("order_id"),
                        rs.getDate("order_date").toLocalDate(),
                        rs.getString("customer_name"),
                        rs.getString("status"),
                        rs.getInt("total_amount")
                    ));
                }
                return list;
            }
        }
    }

}
