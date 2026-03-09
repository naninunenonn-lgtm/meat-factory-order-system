package com.meatfactory.order.model;

/*
 * =============================================
 * クラス: User
 * =============================================
 * 【目的】
 * ユーザー情報を1人分まとめて管理するためのクラス
 *
 * 【役割】
 * ・DBのusersテーブルの1行を表す
 * ・ログインしたユーザー情報を保持する
 * ・セッションに保存して画面で利用する
 *
 * 【種類】
 * モデルクラス（JavaBeans / DTO）
 *
 * 【特徴】
 * ・フィールドでデータを持つ
 * ・getterでデータを取得する
 * ・コンストラクタで初期化できる
 */

import java.io.Serializable;

/*
 * Serializable
 * --------------------------
 * セッションに保存するための準備。
 * Webアプリではオブジェクトをセッションに入れることがあるため、
 * JavaBeansではよく実装する。
 */
public class User implements Serializable {

    /*
     * =============================================
     * フィールド（インスタンス変数）
     * =============================================
     * クラスが持つデータを定義する場所
     *
     * private
     * --------------
     * カプセル化のため外部から直接触れないようにする
     */

    // ユーザーID（DBの主キー）
    private int id;

    // ログインID（ログイン時に入力するID）
    private String loginId;

    // パスワード
    private String password;

    // ユーザーの表示名
    private String userName;

    // 権限（ADMIN / STAFFなど）
    private String role;

    // ユーザーが有効かどうか
    // true  = 有効
    // false = 無効（ログイン不可）
    private boolean active;

    /*
     * =============================================
     * コンストラクタ
     * =============================================
     *
     * コンストラクタとは
     * ----------------------
     * newでインスタンスを作るときに呼ばれる特別なメソッド
     *
     * 例
     * User user = new User();
     *
     * このとき呼ばれる
     */

    // 引数なしコンストラクタ（JavaBeansのルール）
    public User(){}

    /*
     * 引数ありコンストラクタ
     *
     * DBからデータを取得したときに
     * 一度にフィールドを設定するために使う
     *
     * 例
     * User user = new User(1,"admin","pass","管理者","ADMIN",true);
     */
    public User(int id,String loginId,String password,
                String userName,String role,boolean active){

        /*
         * this
         * ------------------
         * このインスタンス自身を指す
         *
         * this.id
         * は
         * このUserオブジェクトのidフィールド
         */

        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.userName = userName;
        this.role = role;
        this.active = active;
    }

    /*
     * =============================================
     * getterメソッド
     * =============================================
     *
     * getterとは
     * --------------------
     * フィールドの値を取得するためのメソッド
     *
     * JSPや他クラスからデータを取得する時に使う
     */

    // ユーザーID取得
    public int getId(){
        return id;
    }

    // ログインID取得
    public String getLoginId(){
        return loginId;
    }

    // パスワード取得
    public String getPassword(){
        return password;
    }

    // ユーザー名取得
    public String getUserName(){
        return userName;
    }

    // 権限取得
    public String getRole(){
        return role;
    }

    /*
     * boolean型のgetterは
     * getではなく
     * is が一般的
     */

    // 有効ユーザーかどうか
    public boolean isActive(){
        return active;
    }
}