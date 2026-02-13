package com.meatfactory.order.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.meatfactory.order.dao.OrderStatusDao;

/**
 * 注文ステータスを更新するサーブレット（更新専用）。
 *
 * URL:
 *   POST /order/status
 *
 * 役割（責務）：
 *   - 画面（orderDetail.jsp）から送られた orderId と status を受け取る
 *   - 入力チェック（不正値は弾く）
 *   - DAOに渡してDB更新する
 *   - 更新後は注文詳細へリダイレクト（PRG）する
 */
@WebServlet("/order/status")
public class OrderStatusUpdateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // =========================================
        // ① 文字化け対策（POSTで送られてくる日本語などの文字を正しく読む）
        // =========================================
        request.setCharacterEncoding("UTF-8");

        // =========================================
        // ② パラメータ取得（JSPのフォームの input/select の name と一致する）
        //    orderId : hidden で送る
        //    status  : select で送る
        // =========================================
        String orderIdStr = request.getParameter("orderId");
        String status = request.getParameter("status");

        // =========================================
        // ③ 必須チェック（空なら画面が壊れている or 改ざんの可能性）
        // =========================================
        if (orderIdStr == null || orderIdStr.isBlank() || status == null || status.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "パラメータが不足しています");
            return;
        }

        // =========================================
        // ④ orderId を数値に変換（できなければ不正）
        // =========================================
        int orderId;
        try {
            orderId = Integer.parseInt(orderIdStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "orderIdが不正です");
            return;
        }

        // =========================================
        // ⑤ status の許可リストチェック（想定外の文字列をDBに入れない）
        //    ※運用で増やすならここに追加
        // =========================================
        boolean allowed =
                "CONFIRMED".equals(status) ||
                "SHIPPED".equals(status) ||
                "CANCELLED".equals(status);

        if (!allowed) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "statusが不正です");
            return;
        }

        // =========================================
        // ⑥ DAOでDB更新（orders.status を更新）
        // =========================================
        try {
            OrderStatusDao dao = new OrderStatusDao();

            // 更新された行数を受け取る（0ならその注文IDが存在しない）
            int updated = dao.updateStatus(orderId, status);

            if (updated == 0) {
                // 注文が存在しない場合
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "注文が見つかりません");
                return;
            }

            // =========================================
            // ⑦ PRG（二重送信防止）
            //    POSTのままforwardすると F5 で再POSTされてしまうので
            //    更新後は GET の注文詳細へリダイレクトする
            // =========================================
            response.sendRedirect(request.getContextPath() + "/order/detail?id=" + orderId);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ステータス更新に失敗しました");
        }
    }
}
