package com.meatfactory.order.servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// DAO / モデルクラスのimport
import com.meatfactory.order.dao.CustomerDao;
import com.meatfactory.order.model.Customer;
import com.meatfactory.order.model.Meat;
import com.meatfactory.order.model.MeatMaster;

/**
 * 【クラス】
 * OrderServlet
 *
 * このクラスは「注文入力画面を表示する」役割を持つServlet。
 *
 * Servletとは：
 * ブラウザからのリクエストを受け取り、処理を行い、画面（JSP）へ渡すJavaプログラム。
 *
 * 【URL】
 * /order
 *
 * 【役割】
 * 注文入力画面を表示する。
 *
 * ※入力チェックや注文確定は別のServlet
 * → OrderConfirmServlet
 *
 * 【処理の流れ】
 *
 * ブラウザ
 *   ↓
 * OrderServlet
 *   ↓
 * MeatMaster / CustomerDao
 *   ↓
 * request.setAttribute()
 *   ↓
 * layout.jsp
 *   ↓
 * orderInput.jsp
 */
@WebServlet("/order")  // このServletが /order URLを担当する
public class OrderServlet extends HttpServlet {

    /**
     * 【メソッド】
     * doGet()
     *
     * HTTP GETリクエストの処理を行うメソッド。
     *
     * 例
     * ブラウザで
     * http://localhost:8081/meat-factory-order-system/order
     * を開いたときに実行される。
     *
     * @param request
     * ブラウザから送られたリクエスト情報
     *
     * @param response
     * ブラウザへ返すレスポンス
     *
     * @throws ServletException
     * Servlet内部エラー
     *
     * @throws IOException
     * 入出力エラー
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // =========================================
        // ① ログインチェック
        // =========================================

        /*
         * HttpSession
         *
         * セッションとは：
         * ユーザーごとのログイン状態などを保存する箱
         *
         * getSession(false)
         *
         * falseの意味：
         * セッションが存在しない場合は新しく作らない
         */
        HttpSession session = request.getSession(false);

        // ログインしていない場合
        if (session == null || session.getAttribute("loginUser") == null) {

            // ログイン画面へリダイレクト
            response.sendRedirect(request.getContextPath() + "/login");

            return;
        }

        // =========================================
        // ② 肉マスタ取得
        // =========================================

        /*
         * MeatMaster.getMeatList()
         *
         * 肉マスタの一覧を取得する。
         *
         * 例
         * 牛ロース
         * 牛カルビ
         * 豚バラ
         */
        List<Meat> meatList = MeatMaster.getMeatList();

        // JSPへ渡す
        request.setAttribute("meatList", meatList);

        // =========================================
        // ③ 取引先一覧取得（DB）
        // =========================================

        try {

            /*
             * CustomerDao
             *
             * DAO = Data Access Object
             *
             * DBアクセス専用クラス
             */
            CustomerDao customerDao = new CustomerDao();

            /*
             * 有効な取引先一覧を取得
             */
            List<Customer> customerList = customerDao.findActive();

            // JSPへ渡す
            request.setAttribute("customerList", customerList);

        } catch (Exception e) {

            // エラー原因をコンソールに出力
            e.printStackTrace();

            // 画面にエラーメッセージを出す
            request.setAttribute(
                "errorMessage",
                "取引先一覧の取得に失敗しました。DB接続を確認してください。"
            );
        }

        // =========================================
        // ④ 注文数量入力用の配列
        // =========================================

        /*
         * 注文数量入力用の配列
         *
         * 肉の種類ごとに数量入力欄を作るための箱
         *
         * 例
         * 牛ロース → quantity[0]
         * 牛カルビ → quantity[1]
         */
        String[] quantities = new String[meatList.size()];

        request.setAttribute("quantities", quantities);

        // =========================================
        // ⑤ 取引先の初期値
        // =========================================

        /*
         * 取引先未選択状態
         */
        request.setAttribute("customerId", "");

        // =========================================
        // ⑥ JSPへ画面表示
        // =========================================

        /*
         * layout.jsp
         * 共通レイアウト
         *
         * orderInput.jsp
         * 注文入力画面
         */
        request.setAttribute("contentPage", "/WEB-INF/jsp/orderInput.jsp");

        request.getRequestDispatcher("/WEB-INF/jsp/common/layout.jsp")
               .forward(request, response);
    }
}