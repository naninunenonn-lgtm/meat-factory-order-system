package com.meatfactory.order.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.meatfactory.order.dao.OrderDetailDao;
import com.meatfactory.order.model.OrderDetail;

/**
 * 注文詳細画面の表示を担当するServlet
 *
 * 【目的】
 * 注文一覧画面などから渡された注文IDをもとに、
 * その注文の詳細情報をDBから取得して表示する。
 *
 * 【URL】
 * GET /order/detail?id=123
 *
 * 【処理の流れ】
 * 1. ログインチェック
 * 2. URLパラメータ id を取得
 * 3. id を int に変換
 * 4. OrderDetailDao を使って注文詳細を取得
 * 5. 取得できたら request に詰めて JSP に渡す
 * 6. 取得できなければ 404 エラー
 *
 * 【MVCの流れ】
 * ブラウザ
 *   ↓
 * OrderDetailServlet
 *   ↓
 * OrderDetailDao
 *   ↓
 * DB
 *   ↓
 * OrderDetail
 *   ↓
 * JSP
 */
@WebServlet("/order/detail")
public class OrderDetailServlet extends HttpServlet {

    /**
     * 注文詳細画面を表示する処理
     *
     * @param request  ブラウザから送られたHTTPリクエスト
     *                 ・URLパラメータ id
     *                 ・セッション情報
     *
     * @param response ブラウザへ返すHTTPレスポンス
     *                 ・画面表示
     *                 ・エラー応答
     *
     * @throws ServletException サーブレット処理エラー
     * @throws IOException      入出力エラー
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ===============================
        // ① ログインチェック
        // 未ログインならログイン画面へ戻す
        // ===============================
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // ===============================
        // ② URLパラメータ id を取得
        // 例: /order/detail?id=123
        // ===============================
        String idStr = request.getParameter("id");

        // id が無い場合は 400 Bad Request
        if (idStr == null || idStr.isEmpty()) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "id が指定されていません"
            );
            return;
        }

        // ===============================
        // ③ id を int に変換
        // 数字でない場合は 400 Bad Request
        // ===============================
        int orderId;
        try {
            orderId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "id が不正です"
            );
            return;
        }

        // ===============================
        // ④ DAOで注文詳細を取得
        // ===============================
        try {
            OrderDetailDao dao = new OrderDetailDao();
            OrderDetail detail = dao.findById(orderId);

            // ===============================
            // ⑤ 注文が存在しない場合は 404 Not Found
            // ===============================
            if (detail == null) {
                response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "注文が見つかりません"
                );
                return;
            }

            // ===============================
            // ⑥ JSPへ渡して表示
            // ===============================
            request.setAttribute("detail", detail);

            // layout.jsp に中身のJSPを差し込むための指定
            request.setAttribute("contentPage", "/WEB-INF/jsp/orderDetail.jsp");

            request.getRequestDispatcher("/WEB-INF/jsp/common/layout.jsp")
                   .forward(request, response);

        } catch (Exception e) {

            // ===============================
            // ⑦ 例外発生時
            // 学習用にコンソールへ出力し、
            // ユーザーには500エラーを返す
            // ===============================
            e.printStackTrace();

            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "注文詳細の取得に失敗しました"
            );
        }
    }
}