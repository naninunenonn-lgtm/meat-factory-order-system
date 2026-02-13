<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>精肉工場システム</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
      rel="stylesheet">

<style>
/* 左メニュー固定レイアウト */
.wrapper {
    display: flex;
    min-height: 100vh;
}

.sidebar {
    width: 220px;
    background: #343a40;
    color: white;
}

.sidebar a {
    color: white;
    text-decoration: none;
    display: block;
    padding: 12px 18px;
}

.sidebar a:hover {
    background: #495057;
}

.content {
    flex: 1;
    padding: 25px;
    background: #f8f9fa;
}
</style>
</head>

<body>

<div class="wrapper">

    <!-- 左メニュー -->
    <div class="sidebar">
        <h5 class="p-3 border-bottom">精肉工場</h5>

        <a href="${pageContext.request.contextPath}/order">
            新規注文
        </a>

        <a href="${pageContext.request.contextPath}/order/list">
            注文一覧
        </a>
        
        <a href="${pageContext.request.contextPath}/master/customer">
    		取引先マスタ
		</a>
        
    </div>

    <!-- 右側：中身差し込み -->
    <div class="content">
        <jsp:include page="${contentPage}" />
    </div>

</div>

</body>
</html>
