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

@WebServlet("/order/confirm")
public class OrderConfirmServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // ① 肉マスタ
        List<Meat> meatList = MeatMaster.getMeatList();

        // ② 入力値取得
        String[] meatTypes = request.getParameterValues("meatType");
        String[] quantities = request.getParameterValues("quantity");

        // ③ 注文リスト作成
        List<OrderItem> orderItemList = new ArrayList<>();
        int totalPrice = 0;

        for (int i = 0; i < meatTypes.length; i++) {
            if (quantities[i] == null || quantities[i].isEmpty()) continue;

            int qty = Integer.parseInt(quantities[i]);
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

        // ④ JSP に渡す
        request.setAttribute("orderItemList", orderItemList);
        request.setAttribute("totalPrice", totalPrice);

        request.getRequestDispatcher("/WEB-INF/jsp/orderConfirm.jsp")
               .forward(request, response);
    }
}
