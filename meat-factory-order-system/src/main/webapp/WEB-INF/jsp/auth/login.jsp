<%-- =========================================================
     page ディレクティブ
     種類 : JSPディレクティブ
     目的 : JSPページの基本設定
     
     contentType
       → ブラウザに返すHTMLの文字コード

     pageEncoding
       → JSPファイル自体の文字コード
========================================================= --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>


<%-- =========================================================
     taglib
     種類 : JSPディレクティブ
     目的 : JSTLタグライブラリを使えるようにする

     prefix="c"
       → JSTLタグを c:if や c:forEach と書くための接頭辞

     uri
       → JSTL coreタグの定義
========================================================= --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>


<!DOCTYPE html>
<html>

<head>

<%-- HTML文字コード設定 --%>
<meta charset="UTF-8">

<%-- ブラウザのタブタイトル --%>
<title>ログイン | Meat Ordering System</title>


<%-- =========================================================
     Bootstrap CSS
     種類 : CSSフレームワーク
     目的 : デザインを整える
========================================================= --%>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

</head>


<%-- =========================================================
     body
     class="bg-light"
     Bootstrapで背景色を薄いグレーにする
========================================================= --%>
<body class="bg-light">


<%-- =========================================================
     container
     Bootstrapレイアウトの基本コンテナ
========================================================= --%>
<div class="container mt-5">

    <%-- 画面中央に配置するためのrow --%>
    <div class="row justify-content-center">

        <%-- 横幅を少し狭くする --%>
        <div class="col-md-5">


            <%-- =====================================================
                 card
                 Bootstrapのカードデザイン
            ===================================================== --%>
            <div class="card shadow">


                <%-- カードのヘッダー部分 --%>
                <div class="card-header bg-primary text-white text-center">

                    <%-- タイトル表示 --%>
                    <h4 class="mb-0">Meat Ordering System</h4>

                </div>


                <%-- カード本文 --%>
                <div class="card-body">


                    <%-- =====================================================
                         c:if
                         種類 : JSTLタグ
                         目的 : 条件分岐

                         ${not empty error}
                         種類 : EL式 (Expression Language)

                         意味
                         error が存在する場合だけ表示
                         
                         errorは LoginServlet の
                         request.setAttribute("error", "...")
                         から渡される
                    ===================================================== --%>
                    <c:if test="${not empty error}">

                        <%-- エラーメッセージ表示用UI --%>
                        <div class="alert alert-danger" role="alert">
                            ${error}
                        </div>

                    </c:if>


                    <%-- =====================================================
                         form
                         種類 : HTMLフォーム
                         目的 : ログイン情報をサーバに送る

                         action
                           → 送信先URL

                         ${pageContext.request.contextPath}
                           → アプリのルートパス

                         /login
                           → LoginServlet
                    ===================================================== --%>
                    <form action="${pageContext.request.contextPath}/login" method="post">


                        <%-- ===============================
                             ログインID入力
                        =============================== --%>
                        <div class="mb-3">

                            <%-- ラベル表示 --%>
                            <label for="loginId" class="form-label">
                                ログインID
                            </label>

                            <%-- 入力欄
                                 name="loginId"
                                 → Servletで取得
                                   request.getParameter("loginId")
                            --%>
                            <input
                                type="text"
                                class="form-control"
                                id="loginId"
                                name="loginId"
                                required>

                        </div>


                        <%-- ===============================
                             パスワード入力
                        =============================== --%>
                        <div class="mb-3">

                            <label for="password" class="form-label">
                                パスワード
                            </label>

                            <%-- type=password
                                 入力文字を隠す
                            --%>
                            <input
                                type="password"
                                class="form-control"
                                id="password"
                                name="password"
                                required>

                        </div>


                        <%-- ===============================
                             ログインボタン
                        =============================== --%>
                        <div class="d-grid">

                            <%-- submit
                                 フォーム送信
                            --%>
                            <button
                                type="submit"
                                class="btn btn-primary">

                                ログイン

                            </button>

                        </div>

                    </form>


                </div>
            </div>


        </div>
    </div>
</div>

</body>
</html>