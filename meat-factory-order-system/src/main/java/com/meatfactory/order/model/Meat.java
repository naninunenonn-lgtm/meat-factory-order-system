package com.meatfactory.order.model;

/**
 * 肉マスタの1レコードを表すクラス（モデル / DTO的な役割）。
 *
 * 例：
 *  code  = "BEEF"
 *  name  = "牛肉"
 *  price = 1000（円/kg）
 *
 * サーブレットやDAOが「肉の情報」を扱うときに、このクラスにまとめて持たせます。
 */
public class Meat {

    // DBや画面で肉を識別するためのコード（例：BEEF）
    private String code;

    // 画面に表示する肉の名称（例：牛肉）
    private String name;

    // 単価（円/kg）
    private int price;

    /**
     * コンストラクタ
     * - new Meat(...) した瞬間に、3つの項目が必ず埋まる（不完全な状態を作りにくい）
     */
    public Meat(String code, String name, int price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    // ===== getter（外から値を読むためのメソッド）=====
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
