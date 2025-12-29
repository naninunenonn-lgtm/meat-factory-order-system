<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>注文結果</title>
	    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<h1>注文結果</h1>

<table border="1">
    <tr>
        <th>肉の種類</th>
        <th>部位</th>
        <th>数量(kg)</th>
        <th>単価(円)</th>
        <th>小計(円)</th>
    </tr>

    <c:forEach var="item" items="${orderItemList}">
        <tr>
            <td>${item.meatName}</td>
            <td>${item.part}</td>
            <td>${item.quantity}</td>
            <td>${item.price}</td>
            <td>${item.subtotal}</td>
        </tr>
    </c:forEach>
</table>

<p><strong>合計金額：${totalPrice} 円</strong></p>

<a href="order">注文画面に戻る</a>
</body>
</html>