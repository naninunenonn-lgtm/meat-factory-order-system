<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<h3 class="mb-3">取引先マスタ</h3>

<div class="card shadow">
    <div class="card-body">

        <!-- 新規作成ボタン（まだ動かない：後で作る） -->
        <div class="mb-3">
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/master/customer/new">
                新規取引先
            </a>
        </div>

        <!-- 一覧テーブル -->
        <table class="table table-bordered table-hover">
            <thead class="table-light">
                <tr>
                    <th style="width:120px;">コード</th>
                    <th>取引先名</th>
                    <th style="width:160px;">操作</th>
                </tr>
            </thead>

            <tbody>
                <c:forEach var="c" items="${customerList}">
                    <tr>
                        <td>${c.code}</td>
                        <td>${c.name}</td>
                        <td>
                            <!-- 編集（次ステップ） -->
                            <a class="btn btn-sm btn-secondary"
                               href="${pageContext.request.contextPath}/master/customer/edit?id=${c.customerId}">
                                編集
                            </a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

    </div>
</div>
