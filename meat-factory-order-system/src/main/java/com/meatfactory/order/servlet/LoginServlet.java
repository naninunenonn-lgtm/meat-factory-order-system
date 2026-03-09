/*
 * =========================================================
 * package
 * =========================================================
 * パッケージ宣言
 *
 * クラスが所属するフォルダのようなもの
 * Javaでは必ずパッケージの中にクラスを置く
 */
package com.meatfactory.order.servlet;


/*
 * =========================================================
 * import
 * =========================================================
 * 他のクラスを使うための宣言
 *
 * Javaでは別パッケージのクラスを使うとき import が必要
 */

/* IOException
 * 種類：クラス
 * 用途：入出力エラー
 */
import java.io.IOException;

/* RequestDispatcher
 * 種類：クラス
 * 用途：JSPへ画面遷移する
 */
import jakarta.servlet.RequestDispatcher;
/* ServletException
 * 種類：クラス
 * 用途：Servlet処理中の例外
 */
import jakarta.servlet.ServletException;
/* WebServlet
 * 種類：アノテーション
 * 用途：このクラスがServletであることを宣言する
 */
import jakarta.servlet.annotation.WebServlet;
/* HttpServlet
 * 種類：クラス
 * 用途：Servletの親クラス
 */
import jakarta.servlet.http.HttpServlet;
/* HttpServletRequest
 * 種類：クラス
 * 用途：ブラウザから送られた情報を持つオブジェクト
 */
import jakarta.servlet.http.HttpServletRequest;
/* HttpServletResponse
 * 種類：クラス
 * 用途：ブラウザへ返す情報を操作する
 */
import jakarta.servlet.http.HttpServletResponse;
/* HttpSession
 * 種類：クラス
 * 用途：ログイン状態などを保持する
 */
import jakarta.servlet.http.HttpSession;

/* UserDao
 * 種類：クラス（DAO）
 * 用途：DBからユーザーを取得する
 */
import com.meatfactory.order.dao.UserDao;
/* User
 * 種類：クラス（モデル）
 * 用途：ユーザー情報を保持
 */
import com.meatfactory.order.model.User;



/*
 * =========================================================
 * @WebServlet
 * =========================================================
 *
 * 種類：アノテーション
 *
 * URLとServletを紐づける
 *
 * /login にアクセスすると
 * このServletが呼ばれる
 */
@WebServlet("/login")



/*
 * =========================================================
 * クラス定義
 * =========================================================
 *
 * public
 * 種類：アクセス修飾子
 * 意味：他のクラスからアクセス可能
 *
 * class
 * 種類：キーワード
 * 意味：クラス定義
 *
 * LoginServlet
 * 種類：クラス名
 *
 * extends
 * 種類：キーワード
 * 意味：継承
 *
 * HttpServlet
 * 種類：クラス（親クラス）
 *
 * つまり
 *
 * LoginServlet は
 * HttpServlet を継承したクラス
 */
public class LoginServlet extends HttpServlet {



    /*
     * =========================================================
     * doPost メソッド
     * =========================================================
     *
     * protected
     * 種類：アクセス修飾子
     *
     * void
     * 種類：戻り値
     * 意味：戻り値なし
     *
     * doPost
     * 種類：メソッド名
     *
     * HttpServletRequest request
     * 種類：オブジェクト
     * 用途：ブラウザから送られた情報
     *
     * HttpServletResponse response
     * 種類：オブジェクト
     * 用途：ブラウザへ返す処理
     *
     * throws
     * 種類：例外宣言
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {



        /*
         * =====================================================
         * request.getParameter
         * =====================================================
         *
         * request
         * 種類：オブジェクト
         *
         * getParameter
         * 種類：メソッド
         *
         * 用途
         * フォームから送信された値を取得
         */

        String loginId = request.getParameter("loginId");

        String password = request.getParameter("password");



        try {

            /*
             * UserDao dao
             *
             * UserDao
             * 種類：クラス
             *
             * dao
             * 種類：変数
             *
             * new
             * 種類：キーワード
             * 用途：インスタンス生成
             */

            UserDao dao = new UserDao();



            /*
             * dao.findByLogin()
             *
             * dao
             * 種類：オブジェクト
             *
             * findByLogin
             * 種類：メソッド
             *
             * 戻り値
             * Userオブジェクト
             */

            User user = dao.findByLogin(loginId,password);



            /*
             * if
             * 種類：制御構文
             */

            if(user == null){

                /*
                 * request.setAttribute
                 *
                 * 種類：メソッド
                 *
                 * 用途
                 * JSPへ値を渡す
                 */

                request.setAttribute("error","ログイン失敗");



                /*
                 * RequestDispatcher
                 * 種類：クラス
                 */

                RequestDispatcher rd =
                        request.getRequestDispatcher("/login.jsp");



                /*
                 * forward
                 * 種類：メソッド
                 *
                 * 同じリクエストで
                 * JSPへ画面遷移
                 */

                rd.forward(request,response);

                return;
            }



            /*
             * HttpSession
             * 種類：クラス
             */

            HttpSession session = request.getSession();



            /*
             * session.setAttribute
             *
             * 種類：メソッド
             *
             * loginUser
             * 種類：セッションキー
             */

            session.setAttribute("loginUser", user);



            /*
             * response.sendRedirect
             *
             * 種類：メソッド
             *
             * 新しいリクエストで
             * 別ページへ移動
             */

            response.sendRedirect("menu");



        } catch(Exception e){

            /*
             * throw
             * 種類：キーワード
             *
             * 例外を投げる
             */

            throw new ServletException(e);
        }
    }
}