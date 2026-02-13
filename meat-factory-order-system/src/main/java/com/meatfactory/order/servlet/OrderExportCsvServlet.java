package com.meatfactory.order.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.meatfactory.order.dao.OrderExportDao;
import com.meatfactory.order.model.OrderItemCsvRow;

/**
 * 帳票CSV（明細付き）をダウンロードさせるサーブレット。
 *
 * URL:
 *   GET /order/exportCsv?customerId=...&from=...&to=...
 *
 * 重要ポイント：
 * - JSPへ forward しない
 * - response に CSV文字列を直接書き込む
 * - Content-Disposition を付けると「ファイルダウンロード」になる
 */
@WebServlet("/order/exportCsv")
public class OrderExportCsvServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // =========================================================
        // ① 検索条件を取得する（一覧画面と同じクエリパラメータ）
        // =========================================================
        String customerIdStr = request.getParameter("customerId");
        String fromStr = request.getParameter("from");
        String toStr = request.getParameter("to");

        // customerId（空なら null。数字でなければ null 扱い）
        Long customerId = null;
        if (customerIdStr != null && !customerIdStr.isBlank()) {
            try {
                customerId = Long.parseLong(customerIdStr);
            } catch (NumberFormatException e) {
                // 改ざんや不正値。今回は「条件なし（null）」として続行
                customerId = null;
            }
        }

        // from/to（日付が空なら null。壊れていても null 扱い）
        LocalDate from = null;
        LocalDate to = null;
        try {
            if (fromStr != null && !fromStr.isBlank()) from = LocalDate.parse(fromStr);
            if (toStr != null && !toStr.isBlank()) to = LocalDate.parse(toStr);
        } catch (Exception e) {
            // 日付形式が壊れていても落とさない（学習用に簡易に）
            from = null;
            to = null;
        }

        // =========================================================
        // ② DAOを使ってDBから「明細1行 = CSV1行」のリストを取得
        // =========================================================
        List<OrderItemCsvRow> rows;
        try {
            OrderExportDao dao = new OrderExportDao();
            rows = dao.findDetailRows(customerId, from, to);
        } catch (Exception e) {
            // DBの問題などで失敗
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "CSV出力に失敗しました（DB）");
            return;
        }

        // =========================================================
        // ③ レスポンスヘッダ設定（ここが「ダウンロード」の鍵）
        // =========================================================

        // Excel互換重視：
        // - Windows版ExcelはUTF-8が文字化けすることがあるのでMS932が無難
        Charset csvCharset = Charset.forName("MS932");
        
        // ===== ファイル名を「yyyyMMddHHmmss」にする =====
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // 20260213211001 の形式
        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        String timestamp = now.format(formatter);

        // 最終ファイル名
        String fileName = "order_detail_" + timestamp + ".csv";




        // 日本語ファイル名に対応するためエンコード
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");

        // 文字コードをレスポンスへ
        response.setCharacterEncoding(csvCharset.name());

        // Content-Type：CSVとして返す
        response.setContentType("text/csv; charset=" + csvCharset.name());

        // Content-Disposition：attachment を付けるとブラウザがDL扱いにする
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

        // =========================================================
        // ④ CSV本文を書き込む（PrintWriterでresponseへ直接出す）
        // =========================================================
        // response.getOutputStream() を使って、指定文字コードで出力
        try (PrintWriter out = new PrintWriter(response.getOutputStream(), true, csvCharset)) {

            // ④-1 ヘッダ行（列名）
            out.println(String.join(",",
                "注文ID","注文日","取引先コード","取引先名","ステータス",
                "肉コード","肉名称","数量","単価","金額"
            ));

            // ④-2 明細行（1DTO = 1行）
            for (OrderItemCsvRow r : rows) {
                out.println(toCsvLine(
                    r.getOrderId(),
                    r.getOrderDate(),
                    r.getCustomerCode(),
                    r.getCustomerName(),
                    r.getStatus(),
                    r.getMeatCode(),
                    r.getMeatName(),
                    r.getQuantity(),
                    r.getUnitPrice(),
                    r.getAmount()
                ));
            }
        }
    }

    // =========================================================
    // ⑤ CSV用：1行をカンマ区切りにする
    //    - カンマ/改行/ダブルクォートが含まれる値はダブルクォートで囲う
    //    - ダブルクォートは "" にエスケープする
    // =========================================================
    private String toCsvLine(Object... cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sb.append(",");
            String v = (cols[i] == null) ? "" : String.valueOf(cols[i]);
            sb.append(escapeCsv(v));
        }
        return sb.toString();
    }

    private String escapeCsv(String s) {
        // CSVで「囲いが必要」になる条件
        boolean needQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");

        // ダブルクォートがあれば "" に変換
        if (s.contains("\"")) {
            s = s.replace("\"", "\"\"");
        }

        // 必要ならダブルクォートで囲う
        return needQuote ? ("\"" + s + "\"") : s;
    }
}
