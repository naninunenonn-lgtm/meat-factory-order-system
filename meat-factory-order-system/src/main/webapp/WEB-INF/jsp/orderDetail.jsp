<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<meta charset="UTF-8">
<title>注文詳細</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
      rel="stylesheet">
</head>

<body class="bg-light">
<div class="container mt-0">

  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">注文詳細</h3>

    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/order/list">
      一覧へ戻る
    </a>
  </div>

  <div class="card shadow mb-3">
    <div class="card-body">
      <div class="row">
        <div class="col-md-3"><strong>注文ID</strong></div>
        <div class="col-md-9">${detail.orderId}</div>

        <div class="col-md-3"><strong>注文日</strong></div>
        <div class="col-md-9">${detail.orderDate}</div>

        <div class="col-md-3"><strong>取引先</strong></div>
        <div class="col-md-9">
          <c:out value="${detail.customerName}" default="(未設定)"/>
        </div>

        <div class="col-md-3"><strong>ステータス</strong></div>
        <div class="col-md-9">${detail.status}</div>
        
      </div>
      	       <!-- ===== ステータス変更フォーム ===== -->
		<form action="${pageContext.request.contextPath}/order/status"
		      method="post"
		      class="mt-3">


	    <!-- どの注文かサーバへ伝える -->
	    <input type="hidden" name="orderId" value="${detail.orderId}">
	
	    <div class="row g-2 align-items-center">

        <div class="col-auto">
            <label class="form-label mb-0">ステータス変更</label>
        </div>

        <div class="col-auto">
			<select name="status" class="form-select">
			  <option value="CONFIRMED" ${detail.status == 'CONFIRMED' ? 'selected' : ''}>受付済</option>
			  <option value="SHIPPED"   ${detail.status == 'SHIPPED'   ? 'selected' : ''}>出荷済</option>
			  <option value="CANCELLED" ${detail.status == 'CANCELLED' ? 'selected' : ''}>キャンセル</option>
			</select>
        </div>

        <div class="col-auto">
            <button type="submit" class="btn btn-primary">
                更新
            </button>
        </div>

    </div>
</form>
      	
    </div>
  </div>

  <div class="card shadow">
    <div class="card-body">

      <h5 class="mb-3">明細</h5>

      <table class="table table-bordered align-middle">
        <thead class="table-light">
          <tr>
            <th>肉の種類</th>
            <th class="text-end" style="width:140px;">数量</th>
            <th class="text-end" style="width:140px;">単価</th>
            <th class="text-end" style="width:160px;">金額</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="it" items="${detail.items}">
            <tr>
              <td>${it.meatName}</td>
              <td class="text-end">${it.quantity}</td>
              <td class="text-end">${it.unitPrice}</td>
              <td class="text-end">${it.amount}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>

      <div class="text-end fw-bold fs-5">
        合計金額：${detail.totalAmount}
      </div>

    </div>
  </div>

</div>
