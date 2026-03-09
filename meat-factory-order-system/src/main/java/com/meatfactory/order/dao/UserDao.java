package com.meatfactory.order.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.meatfactory.order.model.User;

public class UserDao {

    /*
     * =========================================================
     * メソッド: findByLogin
     * =========================================================
     * loginId と password を使って usersテーブルを検索し、
     * 一致する有効ユーザーがいれば User を返す。
     */
    public User findByLogin(String loginId, String password) throws Exception {

        /*
         * SQL文
         * ? はプレースホルダ
         * 後で setString() で値を入れる
         */
        String sql =
            "SELECT id, login_id, password, user_name, role, is_active " +
            "FROM users " +
            "WHERE login_id = ? AND password = ? AND is_active = true";

        /*
         * try-with-resources
         * -------------------
         * Connection / PreparedStatement / ResultSet を
         * 自動で close してくれる
         */
        try (
            Connection conn = DBUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            /*
             * SQLの ? に値をセット
             */
            ps.setString(1, loginId);
            ps.setString(2, password);

            /*
             * SELECT実行
             */
            try (ResultSet rs = ps.executeQuery()) {

                /*
                 * 1件でも検索結果があれば User を作る
                 */
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("login_id"),
                        rs.getString("password"),
                        rs.getString("user_name"),
                        rs.getString("role"),
                        rs.getBoolean("is_active")
                    );
                }
            }
        }

        /*
         * 見つからなかったら nullを返す
         */
        return null;
    }
}