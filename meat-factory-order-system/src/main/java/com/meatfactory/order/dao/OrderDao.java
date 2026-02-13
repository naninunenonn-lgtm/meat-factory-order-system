package com.meatfactory.order.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import com.meatfactory.order.model.OrderItem;

/**
 * 注文（orders + order_item）をDBへ保存するDAO。
 *
 * なぜDAOを作る？
 * - ServletにSQLを書くと長くなり、画面制御（Servlet）とDB操作（SQL）が混ざって読みにくくなる
 * - DB操作はまとめてDAOに隔離すると、修正や再利用が楽になる
 */
public class OrderDao {

    /**
     * 注文を確定保存する（ヘッダ1件 + 明細複数件）
     *
     * ★重要：トランザクション
     * - ヘッダをINSERTしてから明細INSERTする
     * - 明細の途中で失敗したら、ヘッダだけ残るのはNG（不整合）
     * - だから「全部成功したらcommit」「失敗したらrollback」を必ず行う
     *
     * @param customerId 取引先ID（今回は未実装なら null を渡してOK）
     * @param orderDate  受注日
     * @param status     注文ステータス（学習用なら "CONFIRMED" 固定でOK）
     * @param items      注文明細（OrderItemのリスト）
     * @return 登録した orders.order_id
     */
    public int insertOrder(Long customerId, LocalDate orderDate, String status, List<OrderItem> items)
            throws Exception {

        // ① 明細が無い注文は保存できない
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("注文明細がありません");
        }

        // ② orders（ヘッダ）INSERT
        String sqlOrder = """
            INSERT INTO orders (customer_id, order_date, status)
            VALUES (?, ?, ?)
        """;

        // ③ order_item（明細）INSERT
        //    meat_id は meat.code から引く（今のOrderItemがmeatIdを持っていないため）
        //    ※ 将来 OrderItem に meatId を持たせたら、このJOINは不要になる
        String sqlItem = """
            INSERT INTO order_item (order_id, meat_id, quantity, unit_price, amount)
            VALUES (
              ?,                                     -- order_id（ヘッダから）
              (SELECT meat_id FROM meat WHERE code=?),-- meatCodeからmeat_id
              ?,                                     -- quantity
              ?,                                     -- unit_price（注文時点の単価）
              ?                                      -- amount（小計）
            )
        """;

        // ④ 1本のConnectionで処理して「同じトランザクション」にする
        try (Connection con = DBUtil.getConnection()) {

            // ⑤ トランザクション開始（自動commitをOFF）
            con.setAutoCommit(false);

            try {
                // =========================
                // (A) ヘッダ INSERT → order_id を取得
                // =========================
                int orderId;

                try (PreparedStatement ps = con.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {

                    // customer_id（未実装ならnullでもOK）
                    if (customerId == null) {
                        ps.setNull(1, java.sql.Types.BIGINT);
                    } else {
                        ps.setLong(1, customerId);
                    }

                    // order_date
                    ps.setDate(2, java.sql.Date.valueOf(orderDate));

                    // status
                    ps.setString(3, status);

                    // 実行
                    ps.executeUpdate();

                    // 自動採番された order_id を取得
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new IllegalStateException("order_id の取得に失敗しました");
                        }
                        orderId = rs.getInt(1);
                    }
                }

                // =========================
                // (B) 明細をループしてINSERT
                // =========================
                try (PreparedStatement psItem = con.prepareStatement(sqlItem)) {

                    for (OrderItem item : items) {

                        // 1) DBに入れる値を作る
                        String meatCode = item.getMeatCode(); // 例：BEEF
                        int qty = item.getQuantity();
                        int unitPrice = item.getPrice();
                        int amount = qty * unitPrice;

                        // 2) 0以下は入れない（念のため）
                        if (qty <= 0) continue;

                        // 3) パラメータセット
                        psItem.setInt(1, orderId);
                        psItem.setString(2, meatCode);
                        psItem.setInt(3, qty);
                        psItem.setInt(4, unitPrice);
                        psItem.setInt(5, amount);

                        // 4) バッチに貯める（まとめて実行するため）
                        psItem.addBatch();
                    }

                    // まとめて実行（INSERTが複数回走る）
                    psItem.executeBatch();
                }

                // =========================
                // (C) ここまで全部成功 → commit（確定）
                // =========================
                con.commit();

                return orderId;

            } catch (Exception e) {
                // 失敗したらrollback（ヘッダも明細も全て無かったことにする）
                con.rollback();
                throw e;

            } finally {
                // 念のためautoCommitを元に戻す（事故防止）
                con.setAutoCommit(true);
            }
        }
    }
}
