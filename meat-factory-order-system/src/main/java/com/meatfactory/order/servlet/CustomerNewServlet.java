package com.meatfactory.order.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 新規取引先入力画面を表示するだけのサーブレット
 *
 * URL:
 *   GET /master/customer/new
 *
 * 役割：
 *   「画面を出すだけ」
 *   DB操作はまだしない
 */
@WebServlet("/master/customer/new")
public class CustomerNewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ============================
        // 入力画面をlayout経由で表示
        // ============================
        request.setAttribute("contentPage", "/WEB-INF/jsp/master/customerForm.jsp");

        request.getRequestDispatcher("/WEB-INF/jsp/common/layout.jsp")
               .forward(request, response);
    }
}
