package com.meatfactory.order.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * 注文ステータス更新専用DAO。
 *
 * 役割：
 *   - orders テーブルの status を更新するだけ
 *   - SQLをここに閉じ込めて、Servletをスッキリさせる
 */
public class OrderStatusDao {

    /**
     * 指定した orderId の status を更新する。
     *
     * @param orderId 注文ID（PK）
     * @param status  新しいステータス
     * @return 更新された行数（0なら対象の注文が存在しない）
     */
    public int updateStatus(int orderId, String status) throws Exception {

        // =========================================
        // ① SQL（? は後で setString / setInt で埋める）
        // =========================================
        String sql = """
            UPDATE orders
               SET status = ?
             WHERE order_id = ?
        """;

        // =========================================
        // ② try-with-resources で close を自動化
        //    con/ps は処理終了時に自動で閉じられる
        // =========================================
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // =========================================
            // ③ ? に値をセット（順番が重要）
            //    1番目の ? → status
            //    2番目の ? → orderId
            // =========================================
            ps.setString(1, status);
            ps.setInt(2, orderId);

            // =========================================
            // ④ executeUpdate は「更新された行数」を返す
            // =========================================
            return ps.executeUpdate();
        }
    }
}
