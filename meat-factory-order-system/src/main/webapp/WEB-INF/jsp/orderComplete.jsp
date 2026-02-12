<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>



<meta charset="UTF-8">
<title>注文確定</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
      rel="stylesheet">
</head>

<body class="bg-light">

<div class="container mt-5">
  <div class="row justify-content-center">
    <div class="col-md-8">

      <div class="card shadow">
        <div class="card-body">

          <!-- 完了メッセージ -->
          <h2 class="text-success text-center mb-4">
            注文が確定しました
          </h2>

          <p class="text-center">
            ご注文ありがとうございました。
          </p>

          <!-- ========================= -->
          <!-- ここから「確定した注文一覧」表示 -->
          <!-- OrderCompleteServlet#doGet で request に入れたデータを表示 -->
          <!-- ========================= -->

			<c:if test="${not empty orderId}">
			  <p class="text-center">受付番号：${orderId}</p>
			</c:if>

          <c:if test="${not empty orderItemList}">
            <table class="table table-bordered mt-4">
              <thead class="table-light">
                <tr>
                  <th>肉の種類</th>
                  <th class="text-end">数量(kg)</th>
                  <th class="text-end">単価</th>
                  <th class="text-end">小計</th>
                </tr>
              </thead>

              <tbody>
                <!-- 確定した注文を1行ずつ表示 -->
                <c:forEach var="item" items="${orderItemList}">
                  <tr>
                    <td>${item.meatName}</td>
                    <td class="text-end">${item.quantity}</td>
                    <td class="text-end">${item.price}</td>
                    <td class="text-end">${item.subtotal}</td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>

            <!-- 合計金額 -->
            <div class="text-end fw-bold fs-5">
              合計金額：${totalPrice} 円
            </div>
          </c:if>

          <!-- 新規注文リンク -->
          <div class="text-center mt-4">
            <a href="${pageContext.request.contextPath}/order"
               class="btn btn-primary">
              新しい注文をする
            </a>
          </div>

        </div>
      </div>

    </div>
  </div>
</div>

