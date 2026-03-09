package com.meatfactory.order.servlet;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


/**
 * LogoutServlet 
 * ログアウト処理を担当するServlet
 *  
 * 【目的】
 * セッションを破棄してログイン状態を終了する。
 *
 * 【処理内容】
 * 1. 現在のセッションを取得
 * 2. セッションが存在する場合は invalidate() で破棄
 * 3. login.jsp にリダイレクト
 *
 * 【URL】
 * /logout
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    /**
     * ログアウト処理（GET）
     *
     * @param request  HTTPリクエスト
     * @param response HTTPレスポンス
     *
     * @throws IOException 入出力エラー
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        // ===============================
        // ① セッション取得
        // ===============================
        // false：セッションが無ければ新規作成しない
        HttpSession session = request.getSession(false);

        // ===============================
        // ② セッション破棄
        // ===============================
        if (session != null) {
            session.invalidate();
        }

        // ===============================
        // ③ ログイン画面へ戻る
        // ===============================
        response.sendRedirect(request.getContextPath() + "/login");
    }
}