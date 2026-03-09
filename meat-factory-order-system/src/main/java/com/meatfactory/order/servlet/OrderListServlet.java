package com.meatfactory.order.servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.meatfactory.order.dao.CustomerDao;
import com.meatfactory.order.dao.OrderListDao;
import com.meatfactory.order.model.OrderSummary;

/**
 * 注文一覧画面の表示を担当するServlet
 *
 * 【目的】
 * 注文一覧画面を表示する。
 * 必要に応じて検索条件（取引先・期間）を受け取り、
 * 条件に合う注文一覧をDBから取得して画面に渡す。
 *
 * 【URL】
 * GET /order/list
 *
 * 【処理の流れ】
 * 1. ログインチェック
 * 2. リクエストパラメータから検索条件を取得
 * 3. 文字列を Long / LocalDate に変換
 * 4. OrderListDao を使って注文一覧を取得
 * 5. CustomerDao を使って取引先一覧を取得
 * 6. request にデータを詰める
 * 7. layout.jsp に forward する
 *
 * 【MVCの流れ】
 * ブラウザ
 *   ↓
 * OrderListServlet
 *   ↓
 * OrderListDao / CustomerDao
 *   ↓
 * DB
 *   ↓
 * request属性
 *   ↓
 * JSP
 */
@WebServlet("/order/list")
public class OrderListServlet extends HttpServlet {

    /**
     * 注文一覧画面を表示する処理
     *
     * @param request  ブラウザから送られたHTTPリクエスト
     *                 ・検索条件(customerId, from, to)
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

        try {
            // ===============================
            // ② 検索条件を取得（すべて任意）
            // ===============================
            String customerIdStr = request.getParameter("customerId");
            String fromStr = request.getParameter("from");
            String toStr = request.getParameter("to");

            // ===============================
            // ③ customerId を Long に変換
            // 空文字や変換失敗時は null にする
            // ===============================
            Long customerId = null;
            if (customerIdStr != null && !customerIdStr.isBlank()) {
                try {
                    customerId = Long.parseLong(customerIdStr);
                } catch (NumberFormatException e) {
                    // 改ざんや入力不正があっても今回は落とさず null 扱い
                    customerId = null;
                }
            }

            // ===============================
            // ④ 日付文字列を LocalDate に変換
            // 空文字や変換失敗時は null にする
            // ===============================
            java.time.LocalDate from = null;
            java.time.LocalDate to = null;

            try {
                if (fromStr != null && !fromStr.isBlank()) {
                    from = java.time.LocalDate.parse(fromStr);
                }
                if (toStr != null && !toStr.isBlank()) {
                    to = java.time.LocalDate.parse(toStr);
                }
            } catch (Exception e) {
                // 日付形式が壊れていても一覧画面を落とさない
                from = null;
                to = null;
            }

            // ===============================
            // ⑤ 注文一覧を取得
            // 検索条件つきでDB検索
            // ===============================
            OrderListDao dao = new OrderListDao();
            List<OrderSummary> list = dao.findByCriteria(customerId, from, to);

            // JSPで使えるよう request に保存
            request.setAttribute("orderList", list);

            // ===============================
            // ⑥ 取引先一覧を取得
            // 検索フォームのプルダウン表示用
            // ===============================
            CustomerDao customerDao = new CustomerDao();
            request.setAttribute("customerList", customerDao.findActive());

            // ===============================
            // ⑦ layout.jsp に表示内容を渡す
            // ===============================
            request.setAttribute("contentPage", "/WEB-INF/jsp/orderList.jsp");
            request.getRequestDispatcher("/WEB-INF/jsp/common/layout.jsp")
                   .forward(request, response);

        } catch (Exception e) {

            // ===============================
            // ⑧ 例外発生時
            // 学習用にコンソール出力
            // ユーザーには500エラーを返す
            // ===============================
            e.printStackTrace();

            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "注文一覧の取得に失敗しました"
            );
        }
    }
}