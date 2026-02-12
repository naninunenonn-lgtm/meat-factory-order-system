package com.meatfactory.order.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.meatfactory.order.model.Meat;

/**
 * meatテーブルにアクセスするDAO。
 * 今回は「meatCodeからmeatIdを引く」だけ使う。
 */
public class MeatDao {

    /**
     * meat.code から meat.meat_id を取得する
     * @return 見つからなければ null
     */
    public Integer findMeatIdByCode(String meatCode) throws Exception {

        String sql = """
            SELECT meat_id
            FROM meat
            WHERE code = ?
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, meatCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("meat_id");
                }
                return null;
            }
        }
    }

	public Meat[] findAll() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}
