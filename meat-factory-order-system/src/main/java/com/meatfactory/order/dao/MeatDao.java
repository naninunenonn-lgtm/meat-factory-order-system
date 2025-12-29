package com.meatfactory.order.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.meatfactory.order.model.Meat;

public class MeatDao {

    // 肉マスタをすべて取得
    public List<Meat> findAll() {

        List<Meat> meatList = new ArrayList<>();

        String sql = """
            SELECT
                meat_code,
                meat_name,
                price
            FROM meat
            ORDER BY meat_code
        """;

        try (
            Connection con = DBUtil.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                Meat meat = new Meat(
                        rs.getString("meat_code"),
                        rs.getString("meat_name"),
                        rs.getInt("price")
                );
                meatList.add(meat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return meatList;
    }
}
