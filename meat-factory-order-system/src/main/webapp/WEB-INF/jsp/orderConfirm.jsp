<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<meta charset="UTF-8">
<title>注文内容確認</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
      rel="stylesheet">
</head>

<body class="bg-light">

<div class="container mt-0">
  <div class="row justify-content-center">
    <div class="col-md-8">

      <div class="card shadow">
        <div class="card-body">

          <h3 class="card-title text-center mb-4">
            注文内容の確認
          </h3>

          <table class="table table-bordered align-middle">
            <thead class="table-light">
              <tr>
                <th>肉の種類</th>
                <th>数量（kg）</th>
                <th>単価（円）</th>
                <th>小計（円）</th>
              </tr>
            </thead>
            <tbody>
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

          <div class="text-end fs-5 fw-bold mt-3">
            合計金額：${totalPrice} 円
          </div>

          <div class="d-flex justify-content-between mt-4">

            <form action="${pageContext.request.contextPath}/order" method="get">
              <button type="submit" class="btn btn-secondary">
                入力画面に戻る
              </button>
            </form>

            <form action="${pageContext.request.contextPath}/order/complete" method="post">
              <!-- ★取引先IDを次へ渡す -->
  			<input type="hidden" name="customerId" value="${customerId}">
            
            
              <c:forEach var="item" items="${orderItemList}">
                <input type="hidden" name="meatCode" value="${item.meatCode}">
                <input type="hidden" name="quantity" value="${item.quantity}">
              </c:forEach>

              <button type="submit" class="btn btn-primary">
                注文を確定する
              </button>
            </form>

          </div>

        </div>
      </div>

    </div>
  </div>
</div>

</body>
</html>
