package com.meatfactory.order.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DB接続を作るための共通ユーティリティ。
 *
 * DAO（例：MeatDao）は、毎回DB接続が必要になるので
 * 「接続の作り方」をここにまとめておくとコードが散らばらずに済みます。
 */
public class DBUtil {

    // 接続先（JDBC URL）
    // 例：localhost の PostgreSQL、ポート5432、DB名 meat_factory
    private static final String URL =
        "jdbc:postgresql://localhost:5432/meat_factory";

    // DBユーザ
    private static final String USER = "postgres";

    // DBパスワード
    private static final String PASSWORD = "postgres";

    /**
     * DB接続（java.sql.Connection）を取得する
     *
     * @return Connection（接続）
     * @throws SQLException 接続できない場合
     */
    public static Connection getConnection() throws SQLException {

        // ① JDBCドライバのロード
        //    ここで失敗する場合：
        //    - PostgreSQL JDBCドライバがビルドパスに入っていない
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBCドライバが見つかりません（クラスパス未配備）", e);
        }

        // ② URL/ユーザ/パスワードで接続を作成して返す
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
