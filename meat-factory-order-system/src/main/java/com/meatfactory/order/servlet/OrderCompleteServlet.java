package com.meatfactory.order.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.meatfactory.order.model.Meat;
import com.meatfactory.order.model.MeatMaster;
import com.meatfactory.order.model.OrderItem;

/**
 * 「注文を確定する」処理と、完了画面表示を担当するサーブレット。
 *
 * URL:
 *   POST /order/complete  -> 確定処理（本来はここでDB保存）
 *   GET  /order/complete  -> 完了画面の表示
 *
 * ポイント：
 * - confirm.jsp からは hidden で meatCode / quantity が送られてくる
 * - 二重送信（F5更新など）を防ぐため、POSTの後は redirect して GET で表示する（PRG）
 */
@WebServlet("/order/complete")
public class OrderCompleteServlet extends HttpServlet {

    /**
     * 確定ボタン押下で呼ばれる（POST）
     * 1) hidden の値を受け取る（配列）
     * 2) サーバ側で再チェック＆表示用データを再構築する（改ざん対策）
     * 3) （本来はここでDB保存）
     * 4) PRGのため redirect する
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ① 文字化け対策（POSTパラメータをUTF-8で読む）
        request.setCharacterEncoding("UTF-8");

        // ② confirm.jsp の hidden（同名が複数）を配列で受け取る
        //    getParameter() だと先頭1件しか取れないので getParameterValues() を使う
        String[] meatCodes = request.getParameterValues("meatCode");
        String[] quantities = request.getParameterValues("quantity");

        // ③ 不正リクエストの最低限チェック
        //    ・null ならパラメータが来ていない
        //    ・長さ不一致なら、途中で改ざん/欠落している可能性がある
        if (meatCodes == null || quantities == null || meatCodes.length != quantities.length) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "注文データが不正です");
            return;
        }

        // ④ hidden はユーザーが改ざん可能なので、サーバ側で「正しい注文内容」を作り直す
        //    - meatCode がマスタに存在するか
        //    - quantity が数値か、0より大きいか
        //    - 単価は hidden を信用せず、必ずマスタから取得する
        List<Meat> meatList = MeatMaster.getMeatList();  // 肉マスタ（単価・名前の正）
        List<OrderItem> finalItems = new ArrayList<>();  // 完了画面に出す「確定した注文一覧」
        int total = 0;                                  // 合計金額

        for (int i = 0; i < meatCodes.length; i++) {

            // (A) 1行分のコード・数量（文字列）を取り出す
            String code = meatCodes[i];
            String qtyStr = quantities[i];

            // (B) 数量を数値に変換できるかチェック（できないなら無視）
            int qty;
            try {
                qty = Integer.parseInt(qtyStr);
            } catch (NumberFormatException e) {
                // hiddenのquantityが改ざんされて文字になっていた等
                continue;
            }

            // (C) 0以下は注文として扱わない（0は「注文なし」）
            if (qty <= 0) continue;

            // (D) meatCode がマスタに存在するか確認し、存在する肉の情報を取る
            Meat meat = null;
            for (Meat m : meatList) {
                if (m.getCode().equals(code)) {
                    meat = m;
                    break;
                }
            }
            // マスタに存在しないコードなら不正なので無視
            if (meat == null) continue;

            // (E) OrderItem を作る
            //     ※単価(price)は必ずマスタの値を使う（hiddenを信用しない）
            OrderItem item = new OrderItem(
                    code,
                    meat.getName(),
                    "",          // （あなたのOrderItemの設計で必要なら部位等。不要なら後で整理OK）
                    qty,
                    meat.getPrice()
            );

            // (F) 確定リストへ追加し、合計に加算する
            finalItems.add(item);
            total += item.getSubtotal();
        }

        // ⑤ 最終的に1件も残らないなら「注文なし」扱い
        if (finalItems.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "注文がありません");
            return;
        }
        
     // ★ここで先に session を作る（これがポイント）
        HttpSession session = request.getSession();

        // ⑥ 本来ここでDB保存（あとでDAOをここに差し込む）
     // ⑥ 本来ここでDB保存（DAOを差し込む）
        try {
            com.meatfactory.order.dao.OrderDao orderDao = new com.meatfactory.order.dao.OrderDao();

            // 学習用：取引先はまだ未実装なので null でOK
            Long customerId = null;

            // orders.order_date は今日にする（入力画面で日付を選ぶなら後で差し替え）
            java.time.LocalDate orderDate = java.time.LocalDate.now();

            // ステータスは学習用に固定
            String status = "CONFIRMED";

            // DBへ保存（戻り値は生成された order_id）
            int orderId = orderDao.insertOrder(customerId, orderDate, status, finalItems);

            // 完了画面に「受付番号」として出したければ session に積む
            session.setAttribute("orderId", orderId);

        } catch (Exception e) {
            // DB保存に失敗した場合：500で落とすより、原因を出す方が学習しやすい
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB保存に失敗しました");
            return;
        }



        // ⑦ PRG（二重送信防止）のため、表示用データを一時的に session に置く
        //    redirect すると request属性は消えるため、次のGETで取り出せる場所に置く
//        HttpSession session = request.getSession();
        session.setAttribute("orderItemList", finalItems);
        session.setAttribute("totalPrice", total);

        // ⑧ POSTのままforwardすると、F5更新で同じPOSTが再送され「二重登録」になる
        //    そのため redirect で GET に切り替える（PRGパターン）
        response.sendRedirect(request.getContextPath() + "/order/complete");
    }

    /**
     * 完了画面の表示（GET）
     * - doPost で session に置いたデータを取り出して JSP に渡す
     * - 1回表示したら session から削除（再表示/戻るでの混乱防止）
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ① session から、確定直後に保存したデータを取り出す
        HttpSession session = request.getSession();
        Object items = session.getAttribute("orderItemList");
        Object total = session.getAttribute("totalPrice");

        // ② 1回表示したら消す（同じ完了画面を何度も出さないため）
        session.removeAttribute("orderItemList");
        session.removeAttribute("totalPrice");

        // ③ JSPで表示できるように request属性へ移す
        request.setAttribute("orderItemList", items);
        request.setAttribute("totalPrice", total);

        // ④ 完了JSPへ遷移（forward）
        request.getRequestDispatcher("/WEB-INF/jsp/orderComplete.jsp")
               .forward(request, response);
    }
}
