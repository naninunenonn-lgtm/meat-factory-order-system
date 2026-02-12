package com.meatfactory.order.model;

/**
 * 注文明細（1行分）を表すクラス。
 *
 * 目的：
 * 1) JSP表示（肉名、数量、単価、小計）に使う
 * 2) DB登録（order_itemへINSERT）に使う → meatId / cutId が必要
 *
 * ポイント：
 * - 画面表示用の「meatCode/meatName」は残す（既存JSP互換）
 * - DB登録用の「meatId/cutId」を追加する
 * - cutId は未実装（部位未選択）でも動けるように null 許容にする
 */
public class OrderItem {

    // ===== DB登録用 =====

    /** meat.meat_id（DBの主キー） */
    private Integer meatId;

    /** meat_cut.cut_id（部位の主キー）※未使用ならnull */
    private Long cutId;

    // ===== 画面表示用（既存のまま） =====

    /** 肉コード（例：BEEF） */
    private String meatCode;

    /** 肉名称（例：牛肉） */
    private String meatName;

    /** 部位名など（現状は未使用） */
    private String part;

    /** 数量（kg） */
    private int quantity;

    /** 単価（円/kg） */
    private int price;

    /**
     * 既存互換コンストラクタ（今まで通り画面用だけで作れる）
     * - 既存コードを壊さないために残す
     */
    public OrderItem(String meatCode, String meatName, String part, int quantity, int price) {
        this.meatCode = meatCode;
        this.meatName = meatName;
        this.part = part;
        this.quantity = quantity;
        this.price = price;
    }

    /**
     * DB登録まで視野に入れたコンストラクタ
     * - meatId/cutId も一緒に保持できる
     */
    public OrderItem(Integer meatId, Long cutId, String meatCode, String meatName, String part,
                     int quantity, int price) {
        this.meatId = meatId;
        this.cutId = cutId;
        this.meatCode = meatCode;
        this.meatName = meatName;
        this.part = part;
        this.quantity = quantity;
        this.price = price;
    }

    // ===== DB用 getter/setter =====

    public Integer getMeatId() {
        return meatId;
    }

    public void setMeatId(Integer meatId) {
        this.meatId = meatId;
    }

    public Long getCutId() {
        return cutId;
    }

    public void setCutId(Long cutId) {
        this.cutId = cutId;
    }

    // ===== 既存 getter（JSP互換） =====

    public String getMeatCode() {
        return meatCode;
    }

    public String getMeatName() {
        return meatName;
    }

    public String getPart() {
        return part;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    /**
     * 小計（数量 × 単価）
     * - JSPの ${item.subtotal} はこのメソッドが呼ばれる
     */
    public int getSubtotal() {
        return quantity * price;
    }
}