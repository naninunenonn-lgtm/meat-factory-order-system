<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>精肉工場 受発注システム - 注文入力</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="bg-light">


<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">

            <!-- カード開始 -->
            <div class="card shadow">
                <div class="card-header bg-primary text-white text-center">
                    <h4 class="mb-0">精肉工場 受発注システム</h4>
                </div>

                <div class="card-body">

                    <!-- エラーメッセージ -->
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">
                            ${errorMessage}
                        </div>
                    </c:if>

                    <c:if test="${not empty errorList}">
                        <div class="alert alert-danger">
                            <ul class="mb-0">
                                <c:forEach var="err" items="${errorList}">
                                    <li>${err}</li>
                                </c:forEach>
                            </ul>
                        </div>
                    </c:if>

                    <!-- フォーム -->
                    <form action="${pageContext.request.contextPath}/order/confirm" method="post" novalidate>

                        <table class="table table-bordered align-middle">
                            <thead class="table-secondary">
                                <tr>
                                    <th>肉の種類</th>
                                    <th>単価（円/kg）</th>
                                    <th style="width: 150px;">注文数量（kg）</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="meat" items="${meatList}" varStatus="status">
                                    <tr class="${not empty errorRowFlags and errorRowFlags[status.index] ? 'table-danger' : ''}">
                                        <td>${meat.name}</td>
                                        <td>${meat.price}</td>
                                        <td>
                                            <input type="hidden" name="meatType" value="${meat.code}">
                                            <input type="number"
                                                   name="quantity"
                                                   class="form-control"
                                                   value="${not empty quantities ? quantities[status.index] : ''}">
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>

                        <div class="text-center mt-4">
                            <button type="submit" class="btn btn-success btn-lg">
                                注文内容を確認する
                            </button>
                        </div>

                    </form>

                </div>
            </div>
            <!-- カード終了 -->

        </div>
    </div>
</div>

</body>
</html>
