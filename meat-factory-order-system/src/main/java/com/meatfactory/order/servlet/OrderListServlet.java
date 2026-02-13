package com.meatfactory.order.servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.meatfactory.order.dao.CustomerDao;
import com.meatfactory.order.dao.OrderListDao;
import com.meatfactory.order.model.OrderSummary;

/**
 * 注文一覧画面の「入口」になるサーブレット。
 *
 * URL:
 *   GET /order/list
 *
 * 処理の流れ（MVC）：
 *   ブラウザ → Servlet → DAO → DB
 *                          ↓
 *                        DTO(List)
 *                          ↓
 *                        JSP
 */
@WebServlet("/order/list")
public class OrderListServlet extends HttpServlet {

    /**
     * 一覧画面表示処理
     * - DBから一覧取得
     * - requestに詰める
     * - JSPへforward
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
        	// ① 検索条件（全部任意）
        	String customerIdStr = request.getParameter("customerId");
        	String fromStr = request.getParameter("from");
        	String toStr = request.getParameter("to");

        	// customerId（空ならnull）
        	Long customerId = null;
        	if (customerIdStr != null && !customerIdStr.isBlank()) {
        	    try {
        	        customerId = Long.parseLong(customerIdStr);
        	    } catch (NumberFormatException e) {
        	        customerId = null; // 改ざん等は今回は無視（厳密にするならエラー）
        	    }
        	}

        	// 日付（空ならnull）
        	java.time.LocalDate from = null;
        	java.time.LocalDate to = null;

        	try {
        	    if (fromStr != null && !fromStr.isBlank()) from = java.time.LocalDate.parse(fromStr);
        	    if (toStr != null && !toStr.isBlank()) to = java.time.LocalDate.parse(toStr);
        	} catch (Exception e) {
        	    // 日付の形式が壊れていても落とさない（厳密にするならエラー）
        	    from = null; to = null;
        	}

        	// ② 一覧取得（検索条件つき）
        	OrderListDao dao = new OrderListDao();
        	List<OrderSummary> list = dao.findByCriteria(customerId, from, to);
        	request.setAttribute("orderList", list);

        	// ③ 取引先プルダウン用（必須）
        	CustomerDao customerDao = new CustomerDao();
        	request.setAttribute("customerList", customerDao.findActive());

        	// ④ layoutで表示
        	request.setAttribute("contentPage", "/WEB-INF/jsp/orderList.jsp");
        	request.getRequestDispatcher("/WEB-INF/jsp/common/layout.jsp")
        	       .forward(request, response);



        } catch (Exception e) {

            // 学習用：原因をコンソールへ出す
            e.printStackTrace();

            // ユーザー向け：500エラー
            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "注文一覧の取得に失敗しました"
            );
        }
    }
}
