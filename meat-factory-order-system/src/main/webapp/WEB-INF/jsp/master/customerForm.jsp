<%@ page contentType="text/html; charset=UTF-8" %>

<h3 class="mb-3">取引先登録</h3>

<div class="card shadow">
    <div class="card-body">

        <!-- 登録フォーム -->
        <form method="post" action="${pageContext.request.contextPath}/master/customer/create">

            <!-- コード -->
            <div class="mb-3">
                <label class="form-label">取引先コード</label>
                <input type="text" name="code" class="form-control" required>
            </div>

            <!-- 名前 -->
            <div class="mb-3">
                <label class="form-label">取引先名</label>
                <input type="text" name="name" class="form-control" required>
            </div>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-primary">
                    登録
                </button>

                <a class="btn btn-secondary"
                   href="${pageContext.request.contextPath}/master/customer">
                    戻る
                </a>
            </div>

        </form>

    </div>
</div>
