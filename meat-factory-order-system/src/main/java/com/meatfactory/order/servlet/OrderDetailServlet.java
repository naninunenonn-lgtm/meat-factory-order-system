package com.meatfactory.order.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.meatfactory.order.dao.OrderDetailDao;
import com.meatfactory.order.model.OrderDetail;

/**
 * 注文詳細画面を表示するサーブレット。
 * URL:
 *   GET /order/detail?id=123
 */
@WebServlet("/order/detail")
public class OrderDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ① URLパラメータ id を取得
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "id が指定されていません");
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "id が不正です");
            return;
        }

        // ② DAOで詳細取得
        try {
            OrderDetailDao dao = new OrderDetailDao();
            OrderDetail detail = dao.findById(orderId);

            if (detail == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "注文が見つかりません");
                return;
            }

            // ③ JSPへ渡して表示
            request.setAttribute("detail", detail);
            request.setAttribute("contentPage", "/WEB-INF/jsp/orderDetail.jsp");
            request.getRequestDispatcher("/WEB-INF/jsp/common/layout.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "注文詳細の取得に失敗しました");
        }
    }
}
