package com.meatfactory.order.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.meatfactory.order.dao.DBUtil;
import com.meatfactory.order.model.Meat;
import com.meatfactory.order.model.MeatMaster;
import com.meatfactory.order.model.OrderItem;


@WebServlet("/order")
public class OrderServlet extends HttpServlet {

	// ===== 画面表示用 =====
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	System.out.println("★★ OrderServlet doGet が呼ばれました ★★");
    	
        // ===== DB接続確認 =====
        try (Connection con = DBUtil.getConnection()) {
            System.out.println("★★ Servlet から DB接続成功 ★★");
        } catch (Exception e) {
            System.out.println("★★ Servlet から DB接続失敗 ★★");
            e.printStackTrace();
        }    	
    	
    	//肉マスタを呼び出す
    	List<Meat> meatList = MeatMaster.getMeatList();

        // JSP に渡す
        request.setAttribute("meatList", meatList);

        request.getRequestDispatcher("/WEB-INF/jsp/orderInput.jsp")
               .forward(request, response);
    }


     // ===== 注文処理用 =====
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

    	//肉マスタを呼び出す
        List<Meat> meatList = MeatMaster.getMeatList();

        // 入力値を配列で受け取る(リクエストパラメータ取得)
        String[] meatTypes = request.getParameterValues("meatType");
        String[] quantities = request.getParameterValues("quantity");

        //エラー用の箱を作る
        List<String> errorList = new ArrayList<>();

        //エラー行フラグを作る
        boolean[] errorRowFlags = new boolean[quantities.length];

        for (int i = 0; i < quantities.length; i++) {

            String qtyStr = quantities[i];

            // 未入力はOK（注文なし）
            if (qtyStr == null || qtyStr.isEmpty()) {
                continue;
            }

            int qty;

            // ① 数値チェック
            try {
                qty = Integer.parseInt(qtyStr);
            } catch (NumberFormatException e) {
                errorList.add("数量は数値で入力してください（行 " + (i + 1) + "）");
                errorRowFlags[i] = true;
                continue;
            }

            // ② マイナスチェック
            if (qty < 0) {
                errorList.add("数量は0以上で入力してください（行 " + (i + 1) + "）");
                errorRowFlags[i] = true;
                continue;
            }

            // ③ 上限チェック
            if (qty > 100) {
                errorList.add("数量は100kg以下で入力してください（行 " + (i + 1) + "）");
                errorRowFlags[i] = true;
                continue;
            }
        }

        // ④ エラーがあれば画面に戻す
        if (!errorList.isEmpty()) {
            request.setAttribute("errorList", errorList);
            request.setAttribute("errorRowFlags", errorRowFlags);

            // ★JSP 表示用に肉マスタを再セット
            request.setAttribute("quantities", quantities);
            request.setAttribute("meatList", meatList);

            request.getRequestDispatcher("/WEB-INF/jsp/orderInput.jsp")
                   .forward(request, response);
            return;
        }

        // 1つでも注文があるか？
        boolean hasOrder = false;

        for (int i = 0; i < quantities.length; i++) {
            if (quantities[i] != null && !quantities[i].isEmpty()) {
            	int qty = 0;
            	try {
            	    qty = Integer.parseInt(quantities[i]);
            	} catch (NumberFormatException e) {
            	    continue;
            	}

                if (qty > 0) {
                    hasOrder = true;
                    break;
                }
            }
        }

        // 注文が1つもない場合
        if (!hasOrder) {
            request.setAttribute("errorMessage", "数量を1つ以上入力してください。");
            request.setAttribute("meatList", meatList);
            request.getRequestDispatcher("/WEB-INF/jsp/orderInput.jsp")
                   .forward(request, response);
            return;
        }


        // ===== ここから通常処理 =====

        //空の箱を用意する（for の前）
        //合計金額
        int totalPrice = 0;
       // 注文結果リスト
        List<OrderItem> orderItemList = new ArrayList<>();

        // for文でまとめて処理
        // 注文1行ずつ作る（for の中）→ i：注文行（画面の行）
        for (int i = 0; i < meatTypes.length; i++) {

            int qty = 0;
            if (quantities[i] != null && !quantities[i].isEmpty()) {
            	try {
            	    qty = Integer.parseInt(quantities[i]);
            	} catch (NumberFormatException e) {
            	    continue; // 数値でなければスキップ
            	}
            }

            if (qty == 0) {
                continue; // 注文なしはスキップ
            }

//        「今回の注文で選ばれた“肉1種類”を入れる箱」
            Meat selectedMeat = null;

            for (Meat meat : meatList) {
//            	meatList の中の Meat を、1つずつ meat という名前で取り出す
//            	商品棚を1つずつ見て注文されたコードと一致した肉をselectedMeat に入れる
            	if (meatTypes[i].equals(meat.getCode())) {
            		selectedMeat = meat;
            		break;
            	}
            }

            if (selectedMeat == null) {
                continue; // 商品不正
            }

            // ★ OrderItem を作る
            OrderItem item = new OrderItem(
            		selectedMeat.getCode(),  // 肉コード
                    selectedMeat.getName(),  // 肉の種類
                    "",                      // 部位（今は空）
                    qty,                     // 数量
                    selectedMeat.getPrice()  // 単価
            );
            //空の箱に入れる（add）
            orderItemList.add(item);
            totalPrice += item.getSubtotal();
        }

        //箱ごと JSP に渡す
        //orderItemList（List<OrderItem>）を"orderItemList" という名前でJSP に渡している
        request.setAttribute("orderItemList", orderItemList);
        request.setAttribute("totalPrice", totalPrice);

        request.getRequestDispatcher("/WEB-INF/jsp/orderResult.jsp")
               .forward(request, response);
    }
}
