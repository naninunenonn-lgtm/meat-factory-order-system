package com.meatfactory.order.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.meatfactory.order.dao.CustomerDao;

/**
 * 【役割】取引先データの更新処理
 *
 * フロー：
 *   edit.jsp（POST）
 *        ↓
 *   このServlet
 *        ↓
 *   DB更新
 *        ↓
 *   一覧へリダイレクト（PRG）
 */
@WebServlet("/master/customer/update")
public class CustomerUpdateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 日本語POSTを読む準備
        request.setCharacterEncoding("UTF-8");

        try {
            // ============================
            // ① フォーム値を受け取る
            // ============================

            // hidden のID（どの行を更新するか）
            int id = Integer.parseInt(request.getParameter("id"));

            // 入力値
            String code = request.getParameter("code");
            String name = request.getParameter("name");


            // ============================
            // ② DB更新
            // ============================
            CustomerDao dao = new CustomerDao();
            dao.update(id, code, name);


            // ============================
            // ③ PRG（重要）
            // ============================
            // そのままforwardするとF5で再更新される
            // → redirect でGETにする
            response.sendRedirect(request.getContextPath() + "/master/customer");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "更新失敗");
        }
    }
}
