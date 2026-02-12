package com.meatfactory.order.servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.meatfactory.order.model.Meat;
import com.meatfactory.order.model.MeatMaster;

/**
 * 注文入力画面を表示するサーブレット。
 *
 * URL:
 *   GET  /order  -> 入力画面を表示する
 *
 * ここでは「画面を出す」ことだけに役割を絞ります。
 * ※ 入力値のチェック（バリデーション）や確認画面の作成は OrderConfirmServlet が担当します。
 */
@WebServlet("/order")
public class OrderServlet extends HttpServlet {

    /**
     * 入力画面の表示（GET）
     * - 肉マスタを取得して JSP に渡す
     * - 初回表示用に quantities（入力値の箱）も用意して JSP に渡す
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ① 画面に表示する「肉マスタ」を取得する
        //    （例：牛ロース / 価格… などの一覧）
        List<Meat> meatList = MeatMaster.getMeatList();

        // ② JSP が <c:forEach> で回すために request に渡す
        request.setAttribute("meatList", meatList);

        // ③ 初回表示用（入力値の配列）
        //    - エラーで戻った時は OrderConfirmServlet が quantities をセットして戻します
        //    - 初回は空の配列でOK
        String[] quantities = new String[meatList.size()];
        request.setAttribute("quantities", quantities);

        // ④ 入力画面 JSP へ遷移（forward）
        request.getRequestDispatcher("/WEB-INF/jsp/orderInput.jsp")
               .forward(request, response);
    }

    /**
     * doPost は使いません。
     * 入力→確認は /order/confirm にPOSTされ、OrderConfirmServlet が処理します。
     */
}
