package com.meatfactory.order.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static final String URL =
        "jdbc:postgresql://localhost:5432/meat_factory";

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    // DB接続を取得するメソッド
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
