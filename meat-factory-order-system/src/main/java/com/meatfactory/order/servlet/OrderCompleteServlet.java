package com.meatfactory.order.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/order/complete")
public class OrderCompleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String[] meatCodes = request.getParameterValues("meatCode");
        String[] quantities = request.getParameterValues("quantity");

        // ★ 本来ここでDB保存
        // orderDao.insert(...)

        request.getRequestDispatcher("/WEB-INF/jsp/orderComplete.jsp")
               .forward(request, response);
    }
}
