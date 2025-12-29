package com.meatfactory.order.test;

import java.sql.Connection;

import com.meatfactory.order.dao.DBUtil;

public class DBConnectionTest {

    public static void main(String[] args) {
        try (Connection con = DBUtil.getConnection()) {
            System.out.println("DB接続成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
