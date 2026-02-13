package com.meatfactory.order.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.meatfactory.order.model.OrderItemCsvRow;

/**
 * CSV出力（帳票）用のDAO。
 *
 * 一覧用DAO（OrderListDao）と役割が違う：
 * - OrderListDao：注文1件 = 1行（集計あり）
 * - OrderExportDao：明細1件 = 1行（JOINで詳細まで）
 */
public class OrderExportDao {

    /**
     * 帳票CSV用：明細行を検索条件付きで取得する。
     *
     * 返すデータの単位：
     *   1行 = order_item 1件（明細）
     *
     * @param customerId 取引先ID（nullなら全て）
     * @param from 開始日（nullなら条件なし）
     * @param to 終了日（nullなら条件なし）
     */
    public List<OrderItemCsvRow> findDetailRows(Long customerId, LocalDate from, LocalDate to) throws Exception {

        // ① SQLを動的に組み立てる（WHERE条件が任意なので）
        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT
              o.order_id,
              o.order_date,
              COALESCE(c.code, '') AS customer_code,
              COALESCE(c.name, '(未設定)') AS customer_name,
              o.status,
              m.code AS meat_code,
              m.name AS meat_name,
              oi.quantity,
              oi.unit_price,
              oi.amount
            FROM orders o
            LEFT JOIN customer c ON c.customer_id = o.customer_id
            JOIN order_item oi ON oi.order_id = o.order_id
            JOIN meat m ON m.meat_id = oi.meat_id
            WHERE 1=1
        """);

        // ② ? に入れる値を順番にためる（PreparedStatement用）
        List<Object> params = new ArrayList<>();

        // ③ 取引先条件
        if (customerId != null) {
            sql.append(" AND o.customer_id = ? ");
            params.add(customerId);
        }

        // ④ 開始日条件
        if (from != null) {
            sql.append(" AND o.order_date >= ? ");
            params.add(java.sql.Date.valueOf(from));
        }

        // ⑤ 終了日条件
        if (to != null) {
            sql.append(" AND o.order_date <= ? ");
            params.add(java.sql.Date.valueOf(to));
        }

        // ⑥ 並び順（注文ID降順 → 明細ID昇順）
        sql.append(" ORDER BY o.order_id DESC, oi.item_id ASC ");

        // ⑦ DB接続 → SQL実行 → DTOに詰めて返す
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            // ⑧ params を順番に ps の ? にセットする
            //    i+1 なのは JDBCのパラメータ番号は 1始まりだから
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            // ⑨ 実行して結果を読む
            try (ResultSet rs = ps.executeQuery()) {

                List<OrderItemCsvRow> list = new ArrayList<>();

                // ⑩ 1行ずつDTOへ詰める
                while (rs.next()) {
                    OrderItemCsvRow row = new OrderItemCsvRow(
                        rs.getInt("order_id"),
                        rs.getDate("order_date").toLocalDate(),
                        rs.getString("customer_code"),
                        rs.getString("customer_name"),
                        rs.getString("status"),
                        rs.getString("meat_code"),
                        rs.getString("meat_name"),
                        rs.getInt("quantity"),
                        rs.getInt("unit_price"),
                        rs.getInt("amount")
                    );
                    list.add(row);
                }

                // ⑪ 完成した「CSV行リスト」を返す
                return list;
            }
        }
    }
}
