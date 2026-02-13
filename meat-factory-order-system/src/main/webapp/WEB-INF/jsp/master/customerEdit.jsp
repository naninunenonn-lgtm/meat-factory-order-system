<%@ page contentType="text/html; charset=UTF-8" %>

<!--
この画面は「既存データを表示して修正する」画面
新規登録ではないので value にDB値を入れる
-->

<h3 class="mb-3">取引先編集</h3>

<div class="card shadow">
    <div class="card-body">

        <!--
        更新は POST で送る
        理由：DBを書き換える操作はGET禁止（Webの原則）
        -->
        <form method="post" action="${pageContext.request.contextPath}/master/customer/update">

            <!--
            ★超重要
            どのレコードを更新するかサーバに教える必要がある
            → hidden で id を送る
            -->
            <input type="hidden" name="id" value="${customer.customerId}">

            <!-- 取引先コード -->
            <div class="mb-3">
                <label class="form-label">取引先コード</label>

                <!-- DBの値を表示している -->
                <input type="text" name="code" class="form-control"
                       value="${customer.code}" required>
            </div>

            <!-- 取引先名 -->
            <div class="mb-3">
                <label class="form-label">取引先名</label>

                <!-- DBの値を表示している -->
                <input type="text" name="name" class="form-control"
                       value="${customer.name}" required>
            </div>

            <div class="d-flex gap-2">

                <!-- 更新ボタン -->
                <button type="submit" class="btn btn-primary">
                    更新
                </button>

                <!-- 戻る（更新しない） -->
                <a class="btn btn-secondary"
                   href="${pageContext.request.contextPath}/master/customer">
                    戻る
                </a>

            </div>

        </form>

    </div>
</div>
