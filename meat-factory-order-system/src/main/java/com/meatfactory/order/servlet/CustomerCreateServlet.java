package com.meatfactory.order.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.meatfactory.order.dao.CustomerDao;

/**
 * 取引先をDBへ登録するサーブレット
 *
 * URL:
 *
 * 役割：
 *   フォーム値を受け取る
 *   DBへinsert
 *   一覧へリダイレクト（PRG）
 */
@WebServlet("/master/customer/create")
public class CustomerCreateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	    	
        // 未ログインならログイン画面へ戻す
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        // ============================
        // ① フォームから値を受け取る
        // ============================
        String code = request.getParameter("code");
        String name = request.getParameter("name");

        try {
            // ============================
            // ② DAOでDB登録
            // ============================
            CustomerDao dao = new CustomerDao();
            dao.insert(code, name);

            // ============================
            // ③ PRG：一覧へリダイレクト
            // ============================
            response.sendRedirect(request.getContextPath() + "/master/customer");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "登録失敗");
        }
    }
}
