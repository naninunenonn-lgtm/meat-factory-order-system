package com.meatfactory.order.servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.meatfactory.order.dao.CustomerDao;
import com.meatfactory.order.model.Customer;

/**
 * 取引先マスタの一覧画面を表示するサーブレット。
 *
 * URL:
 *   GET /master/customer
 *
 * 役割：
 *   - DBから取引先一覧を取得
 *   - JSPへ渡す
 *   - レイアウトへforward
 */
@WebServlet("/master/customer")
public class CustomerListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // =========================================
            // ① DAO生成（DBアクセス担当クラス）
            // =========================================
            CustomerDao dao = new CustomerDao();

            // =========================================
            // ② DBから取引先一覧を取得
            //    is_active true だけを取得（既存メソッド利用）
            // =========================================
            List<Customer> list = dao.findActive();

            // =========================================
            // ③ JSPに渡す（requestスコープ）
            // =========================================
            request.setAttribute("customerList", list);

            // =========================================
            // ④ layout.jsp に「中身ページ」を指定
            // =========================================
            request.setAttribute("contentPage", "/WEB-INF/jsp/master/customerList.jsp");

            // =========================================
            // ⑤ レイアウト経由で画面表示
            // =========================================
            request.getRequestDispatcher("/WEB-INF/jsp/common/layout.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            // 学習用：原因が分かるよう500を返す
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "取引先一覧取得失敗");
        }
    }
}
