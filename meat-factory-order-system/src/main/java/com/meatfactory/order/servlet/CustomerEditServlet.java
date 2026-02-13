package com.meatfactory.order.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.meatfactory.order.dao.CustomerDao;
import com.meatfactory.order.model.Customer;

/**
 * 【役割】取引先「編集画面」を表示するサーブレット
 *
 * ユーザー操作：
 *   一覧の「編集」ボタン押下
 *
 * ブラウザ：
 *   GET /master/customer/edit?id=1
 *
 * サーバ処理：
 *   ① id を受け取る
 *   ② DBから1件取得
 *   ③ JSPへ渡して表示
 *
 * ★重要：このサーブレットは「表示専用」
 *   DB更新はしない（責務分離）
 */
@WebServlet("/master/customer/edit")
public class CustomerEditServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // ================================
            // ① URLパラメータから id を取得
            // 例： /edit?id=5
            // ================================
            String idStr = request.getParameter("id");

            // idが無い＝URL不正
            if (idStr == null || idStr.isEmpty()) {
                response.sendError(400, "IDが指定されていません");
                return;
            }

            // 文字列 → 数値へ変換
            int id = Integer.parseInt(idStr);


            // ================================
            // ② DAOを使ってDBから1件取得
            // ================================
            CustomerDao dao = new CustomerDao();
            Customer customer = dao.findById(id);

            // DBに存在しない場合
            if (customer == null) {
                response.sendError(404, "取引先が存在しません");
                return;
            }


            // ================================
            // ③ JSPへ渡す（requestスコープ）
            // ================================
            request.setAttribute("customer", customer);

            // layout.jspに「中身として表示するページ」を伝える
            request.setAttribute("contentPage", "/WEB-INF/jsp/master/customerEdit.jsp");

            // layout → edit.jsp の順で表示
            request.getRequestDispatcher("/WEB-INF/jsp/common/layout.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            // 学習用：スタックトレースを必ず見る
            e.printStackTrace();
            response.sendError(500);
        }
    }
}
