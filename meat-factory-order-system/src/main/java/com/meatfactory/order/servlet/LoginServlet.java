package com.meatfactory.order.servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.meatfactory.order.dao.UserDao;
import com.meatfactory.order.model.User;

/**
 * LoginServlet
 * ログイン処理を担当するServlet
 *
 * 【目的】
 * ログイン画面の表示とログイン認証処理を行う。
 * login.jsp から送信されたログインID・パスワードを受け取り、
 * usersテーブルを検索してユーザー認証を行う。
 *
 * 【GET】
 * ログイン画面を表示する
 *
 * 【POST】
 * ログインID・パスワードを受け取り、認証する
 *
 * 【処理の流れ】
 * 1. フォームから loginId / password を取得
 * 2. UserDao を使って usersテーブルを検索
 * 3. ユーザーが存在しない → login.jsp に戻す
 * 4. ユーザーが存在する → セッションに loginUser を保存
 * 5. 注文一覧画面へリダイレクト
 *
 * 【URL】
 * /login
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    /**
     * ログイン画面表示
     *
     * @param request  HTTPリクエスト
     * @param response HTTPレスポンス
     * @throws ServletException サーブレット処理エラー
     * @throws IOException      入出力エラー
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        RequestDispatcher rd =
                request.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp");
        rd.forward(request, response);
    }

    /**
     * ログインフォーム送信（POST）の処理
     *
     * @param request  ブラウザから送信されたリクエスト情報
     *                 ・フォーム入力値
     *                 ・セッション
     *
     * @param response ブラウザへ返すレスポンス
     *                 ・画面遷移
     *
     * @throws ServletException サーブレット処理エラー
     * @throws IOException      入出力エラー
     */
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // ===============================
        // ① フォーム入力値を取得
        // ===============================
        String loginId = request.getParameter("loginId");
        String password = request.getParameter("password");

        // ===============================
        // ② 入力チェック
        // ===============================
        if (loginId == null || loginId.isBlank()
                || password == null || password.isBlank()) {

            request.setAttribute("error", "ログインIDとパスワードを入力してください");

            RequestDispatcher rd =
                    request.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp");

            rd.forward(request, response);
            return;
        }

        try {

            // ===============================
            // ③ DBからユーザー検索
            // ===============================
            UserDao dao = new UserDao();
            User user = dao.findByLogin(loginId, password);

            // ===============================
            // ④ ユーザーが見つからない場合
            // ===============================
            if (user == null) {

                request.setAttribute("error", "ログインIDまたはパスワードが違います");

                RequestDispatcher rd =
                        request.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp");

                rd.forward(request, response);
                return;
            }

            // ===============================
            // ⑤ ログイン成功
            // ===============================
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", user);

            // ===============================
            // ⑥ 注文一覧画面へ移動
            // ===============================
            response.sendRedirect(request.getContextPath() + "/order");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}