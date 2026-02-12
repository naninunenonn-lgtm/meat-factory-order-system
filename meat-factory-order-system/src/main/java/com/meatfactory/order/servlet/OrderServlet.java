package com.meatfactory.order.servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//追加import
import com.meatfactory.order.dao.CustomerDao;
import com.meatfactory.order.model.Customer;
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

	    // ① 画面に表示する「肉マスタ」を取得
	    List<Meat> meatList = MeatMaster.getMeatList();
	    request.setAttribute("meatList", meatList);

	    // ② 取引先一覧をDBから取得して、プルダウンに出す
	    try {
	        CustomerDao customerDao = new CustomerDao();
	        List<Customer> customerList = customerDao.findActive();
	        request.setAttribute("customerList", customerList);
	    } catch (Exception e) {
	        // 学習用：DB不具合は画面で分かるようにメッセージ化
	        e.printStackTrace();
	        request.setAttribute("errorMessage", "取引先一覧の取得に失敗しました。DB接続を確認してください。");
	    }

	    // ③ 初回表示用 quantities
	    String[] quantities = new String[meatList.size()];
	    request.setAttribute("quantities", quantities);

	    // ④ 取引先の初期値（未選択）
	    request.setAttribute("customerId", "");

	    // ⑤ 入力画面へ
	    request.getRequestDispatcher("/WEB-INF/jsp/orderInput.jsp")
	           .forward(request, response);
	}
}
