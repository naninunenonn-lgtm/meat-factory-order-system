package com.meatfactory.order.model;

import java.util.Map;

/**
 * ステータスの「コード → 日本語表示」を管理するクラス
 *
 * DBには CONFIRMED などのコードを保存する
 * 画面には「受付済」など人間の言葉を表示する
 *
 * → 変換の責任を1箇所に集約する
 */
public class OrderStatus {

    // 表示用辞書
    private static final Map<String, String> LABELS = Map.of(
        "CONFIRMED", "受付済",
        "SHIPPED",   "出荷済",
        "CANCELLED", "キャンセル",
        "HOLD", "保留"
    );

    /**
     * コードを日本語表示へ変換する
     * 未定義コードが来ても落ちないよう、そのまま返す
     */
    public static String label(String status) {
        return LABELS.getOrDefault(status, status);
    }
}
