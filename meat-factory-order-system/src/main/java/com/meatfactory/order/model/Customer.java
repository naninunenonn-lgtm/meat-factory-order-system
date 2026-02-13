package com.meatfactory.order.model;

/**
 * 取引先マスタ 1レコードを表すクラス
 */
public class Customer {

    private long customerId;
    private String code;
    private String name;
    private boolean active; // is_active

    // ===============================
    // ① フルコンストラクタ（DB完全一致）
    // ===============================
    public Customer(long customerId, String code, String name, boolean active) {
        this.customerId = customerId;
        this.code = code;
        this.name = name;
        this.active = active;
    }

    // ===============================
    // ② 簡易コンストラクタ（プルダウン用）
    // activeはデフォルトtrue扱い
    // ===============================
    public Customer(long customerId, String code, String name) {
        this(customerId, code, name, true);
    }

    // ===============================
    // getter
    // ===============================
    public long getCustomerId() {
        return customerId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }
}
