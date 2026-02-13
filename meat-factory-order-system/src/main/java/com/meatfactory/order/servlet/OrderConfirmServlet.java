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

        request.setCharacterEncoding("UTF-8");

        List<Meat> meatList = MeatMaster.getMeatList();

        String[] meatTypes = request.getParameterValues("meatType");
        String[] quantities = request.getParameterValues("quantity");

        // ★取引先ID（空でもOK）
        String customerIdStr = request.getParameter("customerId");
        request.setAttribute("customerId", customerIdStr); // 戻し表示用（入力画面でselected維持）

        

        // nullガード
        if (meatTypes == null || quantities == null) {
            request.setAttribute("errorMessage", "入力情報の取得に失敗しました。もう一度やり直してください。");
            // ★入力画面に戻す時に必要なものを全部セットして戻す
            forwardToInput(request, response, meatList, quantities);
            return;
        }

        // ===== バリデーション =====
        List<String> errorList = new ArrayList<>();
        boolean[] errorRowFlags = new boolean[quantities.length];

     // ★取引先が未選択ならエラー（必須）
        if (customerIdStr == null || customerIdStr.trim().isEmpty()) {
            errorList.add("取引先を選択してください。");

            // ★JSPで赤枠にするためのフラグ
            request.setAttribute("customerError", true);
        }

        boolean hasOrder = false; // ★ここで用意

        for (int i = 0; i < quantities.length; i++) {
            String qtyStr = quantities[i];
            if (qtyStr == null || qtyStr.isEmpty()) continue;

            int qty;
            try {
                qty = Integer.parseInt(qtyStr);
            } catch (NumberFormatException e) {
                errorList.add("数量は数値で入力してください（行 " + (i + 1) + "）");
                errorRowFlags[i] = true;
                continue;
            }

            if (qty < 0) {
                errorList.add("数量は0以上で入力してください（行 " + (i + 1) + "）");
                errorRowFlags[i] = true;
                continue;
            }

            if (qty > 100) {
                errorList.add("数量は100kg以下で入力してください（行 " + (i + 1) + "）");
                errorRowFlags[i] = true;
                continue;
            }

            // ★ここまで来た qty は「数値で、0以上、100以下」
            if (qty > 0) {
                hasOrder = true;
            }
        }

        // ★数量が全部0（または未入力）ならエラーに追加
        if (!hasOrder) {
            errorList.add("数量を1つ以上入力してください。");
        }

        // ★エラーが1つでもあれば全部出して戻す
        if (!errorList.isEmpty()) {
            request.setAttribute("errorList", errorList);
            request.setAttribute("errorRowFlags", errorRowFlags);
            forwardToInput(request, response, meatList, quantities);
            return;
        }



        // ===== 確認画面用データ作成 =====
        List<OrderItem> orderItemList = new ArrayList<>();
        int totalPrice = 0;

        for (int i = 0; i < meatTypes.length; i++) {

            int qty = 0;
            if (quantities[i] != null && !quantities[i].isEmpty()) {
                qty = Integer.parseInt(quantities[i]);
            }
            if (qty <= 0) continue;

            Meat selectedMeat = null;
            for (Meat meat : meatList) {
                if (meat.getCode().equals(meatTypes[i])) {
                    selectedMeat = meat;
                    break;
                }
            }
            if (selectedMeat == null) continue;

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

        request.setAttribute("orderItemList", orderItemList);
        request.setAttribute("totalPrice", totalPrice);
        request.setAttribute("customerId", customerIdStr);

        request.setAttribute("contentPage", "/WEB-INF/jsp/orderConfirm.jsp");
        request.getRequestDispatcher("/WEB-INF/jsp/common/layout.jsp")
               .forward(request, response);
    }

    /**
     * 入力画面に戻す時に「必ず必要になるデータ」をまとめてセットして forward する。
     * これを使うと、戻り分岐ごとに setAttribute を書き忘れなくなる。
     */
    private void forwardToInput(HttpServletRequest request, HttpServletResponse response,
            List<Meat> meatList, String[] quantities)
		throws ServletException, IOException {
		
		// ① 入力画面に必須：肉一覧
		request.setAttribute("meatList", meatList);
		
		// ② 入力値復元：数量
		if (quantities != null) {
		request.setAttribute("quantities", quantities);
		}
		
		// ③ 取引先プルダウンの中身（必須）
		try {
		com.meatfactory.order.dao.CustomerDao customerDao = new com.meatfactory.order.dao.CustomerDao();
		request.setAttribute("customerList", customerDao.findActive());
		} catch (Exception e) {
		e.printStackTrace();
		}
		
		// ★取引先の選択状態を維持
		String cid = request.getParameter("customerId");
		request.setAttribute("customerId", cid);

		// ★未選択ならプルダウン赤枠も維持
		if (cid == null || cid.trim().isEmpty()) {
		    request.setAttribute("customerError", true);
		}

		
		// ★ここが重要：layoutで表示する「中身」を入力画面にする
		request.setAttribute("contentPage", "/WEB-INF/jsp/orderInput.jsp");
		
		// ④ layout経由で戻す
		request.getRequestDispatcher("/WEB-INF/jsp/common/layout.jsp")
		.forward(request, response);
}

}


