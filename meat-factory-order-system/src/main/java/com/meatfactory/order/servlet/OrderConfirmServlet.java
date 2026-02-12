package com.meatfactory.order.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.meatfactory.order.model.Meat;
import com.meatfactory.order.model.MeatMaster;
import com.meatfactory.order.model.OrderItem;

/**
 * 注文入力（orderInput.jsp）から送信された内容をチェックし、
 * 「確認画面（orderConfirm.jsp）」を作るサーブレット。
 *
 * URL:
 *   POST /order/confirm
 *
 * このサーブレットの責務：
 *  1) 受け取った入力値を検証（バリデーション）
 *  2) エラーがあれば入力画面に戻す（入力値・エラー内容も渡す）
 *  3) 問題なければ確認画面に表示する注文リストを作って forward
 */
@WebServlet("/order/confirm")
public class OrderConfirmServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ① 文字化け対策（POSTのパラメータをUTF-8で読み取る）
        request.setCharacterEncoding("UTF-8");

        // ② 肉マスタ（表示と単価の正しい情報に使う）
        List<Meat> meatList = MeatMaster.getMeatList();

        // ③ 入力値取得
        //    - meatType は hidden で行数分送られる
        //    - quantity は入力欄（同名が複数）で行数分送られる
        String[] meatTypes = request.getParameterValues("meatType");
        String[] quantities = request.getParameterValues("quantity");

        // ④ nullガード（基本は起きにくいが、安全に）
        if (meatTypes == null || quantities == null) {
            request.setAttribute("errorMessage", "入力情報の取得に失敗しました。もう一度やり直してください。");
            request.setAttribute("meatList", meatList);
            request.getRequestDispatcher("/WEB-INF/jsp/orderInput.jsp").forward(request, response);
            return;
        }

        // ===== ここからバリデーション =====

        // エラー文言をためる箱（JSPで一覧表示する）
        List<String> errorList = new ArrayList<>();

        // どの行がエラーかを示すフラグ（行に赤色を付けるなどに使用）
        boolean[] errorRowFlags = new boolean[quantities.length];

        for (int i = 0; i < quantities.length; i++) {

            String qtyStr = quantities[i];

            // (A) 未入力は「注文なし」とみなし、エラーにしない
            if (qtyStr == null || qtyStr.isEmpty()) {
                continue;
            }

            int qty;

            // (B) 数値変換できるか（数値以外ならエラー）
            try {
                qty = Integer.parseInt(qtyStr);
            } catch (NumberFormatException e) {
                errorList.add("数量は数値で入力してください（行 " + (i + 1) + "）");
                errorRowFlags[i] = true;
                continue;
            }

            // (C) マイナスはエラー
            if (qty < 0) {
                errorList.add("数量は0以上で入力してください（行 " + (i + 1) + "）");
                errorRowFlags[i] = true;
                continue;
            }

            // (D) 上限チェック（学習用ルール：100kgまで）
            if (qty > 100) {
                errorList.add("数量は100kg以下で入力してください（行 " + (i + 1) + "）");
                errorRowFlags[i] = true;
            }
        }

        // (E) エラーがあれば入力画面に戻す
        if (!errorList.isEmpty()) {
            // 入力画面で再表示するため、必要な情報をrequestに詰める
            request.setAttribute("errorList", errorList);
            request.setAttribute("errorRowFlags", errorRowFlags);
            request.setAttribute("quantities", quantities);
            request.setAttribute("meatList", meatList);

            request.getRequestDispatcher("/WEB-INF/jsp/orderInput.jsp").forward(request, response);
            return;
        }

        // ===== ここから確認画面用データ作成 =====

        // 1つも注文が無い（全行 未入力/0）なら入力画面へ戻す
        boolean hasOrder = false;
        for (int i = 0; i < quantities.length; i++) {
            if (quantities[i] == null || quantities[i].isEmpty()) continue;

            try {
                int qty = Integer.parseInt(quantities[i]);
                if (qty > 0) {
                    hasOrder = true;
                    break;
                }
            } catch (NumberFormatException e) {
                // ここには来ない想定（前のチェックで弾いている）だが念のため
            }
        }

        if (!hasOrder) {
            request.setAttribute("errorMessage", "数量を1つ以上入力してください。");
            request.setAttribute("quantities", quantities);
            request.setAttribute("meatList", meatList);
            request.getRequestDispatcher("/WEB-INF/jsp/orderInput.jsp").forward(request, response);
            return;
        }

        // 確認画面に表示する注文リストを作る
        List<OrderItem> orderItemList = new ArrayList<>();
        int totalPrice = 0;

        for (int i = 0; i < meatTypes.length; i++) {

            // quantity が空なら注文なし
            int qty = 0;
            if (quantities[i] != null && !quantities[i].isEmpty()) {
                qty = Integer.parseInt(quantities[i]);
            }
            if (qty <= 0) continue;

            // meatType（コード）から、肉マスタ情報（名前・単価）を探す
            Meat selectedMeat = null;
            for (Meat meat : meatList) {
                if (meat.getCode().equals(meatTypes[i])) {
                    selectedMeat = meat;
                    break;
                }
            }
            if (selectedMeat == null) continue;

            // 注文明細を作る（小計は OrderItem 側のロジックで計算）
            OrderItem item = new OrderItem(
                    selectedMeat.getCode(),
                    selectedMeat.getName(),
                    "",
                    qty,
                    selectedMeat.getPrice()
            );

            orderItemList.add(item);
            totalPrice += item.getSubtotal();
        }

        // JSPへ渡す
        request.setAttribute("orderItemList", orderItemList);
        request.setAttribute("totalPrice", totalPrice);

        // 確認画面へ
        request.getRequestDispatcher("/WEB-INF/jsp/orderConfirm.jsp")
               .forward(request, response);
    }
}
