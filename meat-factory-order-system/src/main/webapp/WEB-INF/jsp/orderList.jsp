<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container">

  <!-- ① タイトル＋ボタン群 -->
  <div class="d-flex justify-content-between align-items-center mb-3">

    <h3 class="mb-0">注文一覧</h3>

    <div class="d-flex gap-2">
      <!-- ② CSV出力ボタン（明細帳票） -->
      <!--
          重要ポイント：
          - 一覧画面に入力した検索条件（customerId/from/to）を
            そのままCSV出力にも適用したいので、URLパラメータを引き継ぐ。
          - param.xxx は「いまのURLのクエリ」を参照する（GETパラメータ）
      -->
      <a class="btn btn-outline-success"
         href="${pageContext.request.contextPath}/order/exportCsv?customerId=${param.customerId}&from=${param.from}&to=${param.to}">
        CSV出力（明細）
      </a>

      <!-- ③ 新規注文へ -->
      <a class="btn btn-secondary" href="${pageContext.request.contextPath}/order">
        新規注文へ
      </a>
    </div>
  </div>

  <!-- ④ 検索フォーム：GETで /order/list に投げる -->
  <!--
      - method="get" にする理由：
        検索条件をURLに残せる（戻る/共有/再検索が楽）
      - action は OrderListServlet（/order/list）
  -->
  <form class="row g-2 align-items-end mb-3" method="get"
        action="${pageContext.request.contextPath}/order/list">

    <div class="col-md-4">
      <label class="form-label">取引先</label>

      <!--
          - name="customerId" の値が GET パラメータになる
          - items="${customerList}" はサーブレットが request に詰めたもの
      -->
      <select name="customerId" class="form-select">
        <option value="">-- 全て --</option>

        <c:forEach var="c" items="${customerList}">
          <!--
              selectedの維持：
              - param.customerId は文字列
              - c.customerId は数値のことがある
              - もしselectedが効かない場合は、Servlet側で selectedCustomerId を文字列で渡す方式に変更
          -->
          <option value="${c.customerId}"
            <c:if test="${param.customerId == c.customerId}">selected</c:if>>
            ${c.code}：${c.name}
          </option>
        </c:forEach>
      </select>
    </div>

    <div class="col-md-3">
      <label class="form-label">開始日</label>
      <!--
          - type="date" なので 2026-02-13 の形式で送られる
          - value="${param.from}" で入力保持
      -->
      <input type="date" name="from" class="form-control" value="${param.from}">
    </div>

    <div class="col-md-3">
      <label class="form-label">終了日</label>
      <input type="date" name="to" class="form-control" value="${param.to}">
    </div>

    <div class="col-md-2 d-grid">
      <button class="btn btn-primary" type="submit">検索</button>
    </div>
  </form>

  <!-- ⑤ 一覧表示 -->
  <div class="card shadow">
    <div class="card-body">

      <!-- データなし -->
      <c:if test="${empty orderList}">
        <div class="alert alert-info mb-0">
          注文データがありません。
        </div>
      </c:if>

      <!-- データあり -->
      <c:if test="${not empty orderList}">
        <table class="table table-bordered table-hover align-middle">
          <thead class="table-light">
            <tr>
              <th style="width:120px;">注文ID</th>
              <th style="width:160px;">注文日</th>
              <th>取引先</th>
              <th style="width:160px;">ステータス</th>
              <th class="text-end" style="width:160px;">合計金額</th>
            </tr>
          </thead>

          <tbody>
            <c:forEach var="o" items="${orderList}">
              <tr>
                <td>
                  <!-- 詳細画面へ -->
                  <a href="${pageContext.request.contextPath}/order/detail?id=${o.orderId}">
                    ${o.orderId}
                  </a>
                </td>
                <td>${o.orderDate}</td>
                <td><c:out value="${o.customerName}" default="(未設定)"/></td>
                <td>${o.status}</td>
                <td class="text-end">${o.totalAmount}</td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </c:if>

    </div>
  </div>

</div>
